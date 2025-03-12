package com.naer.naerapigateway;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.ValueFilter;
import com.alibaba.nacos.api.remote.response.ErrorResponse;
import com.naer.chanapiclientsdk.utils.SignUtils;
import com.naer.naerApiCommon.model.entity.InterfaceInfo;
import com.naer.naerApiCommon.model.entity.User;
import com.naer.naerApiCommon.model.entity.UserInterfaceInfo;
import com.naer.naerApiCommon.service.InnerInterfaceInfoService;
import com.naer.naerApiCommon.service.InnerUserInterfaceInfoService;
import com.naer.naerApiCommon.service.InnerUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.bouncycastle.cms.PasswordRecipientId;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.springframework.core.io.buffer.DataBuffer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * 全局过滤器
 */
//全局过滤器
@Slf4j
@Component
public class CustomGlobalFilter implements GlobalFilter, Ordered {

    @DubboReference
    private InnerUserService innerUserService;

    @DubboReference
    private InnerUserInterfaceInfoService innerUserInterfaceInfoService;

    @DubboReference
    private InnerInterfaceInfoService innerInterfaceInfoService;

    private static final List<String> IP_WHITE_LIST = Arrays.asList("127.0.0.1");

    private static final String INTERFACE_HOST = "http://localhost:8123";

    //1.用户发送请求到API网关
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //2.请求日志
        ServerHttpRequest request = exchange.getRequest();
        String path = INTERFACE_HOST + request.getPath().value();
        String method = request.getMethod().toString();
        log.info("请求唯一标识："+request.getId());
        log.info("请求路径："+path);
        log.info("请求方法："+method);
        log.info("请求参数："+request.getQueryParams());
        String hostString = request.getLocalAddress().getHostString();
        log.info("请求来源地址："+hostString);
        log.info("请求来源地址："+request.getRemoteAddress());
        ServerHttpResponse response = exchange.getResponse();
        //3.黑白名单
        if(!IP_WHITE_LIST.contains(hostString)){
            response.setStatusCode(HttpStatus.FORBIDDEN);
            return response.setComplete();
        }
        //4.用户鉴权(ak,sk 是否合法)
        HttpHeaders headers = request.getHeaders();
        String accessKey = headers.getFirst("accessKey");
        String nonce = headers.getFirst("nonce");
        String timestamp = headers.getFirst("timestamp");
        String sign = headers.getFirst("sign");
        String body = headers.getFirst("body");
        //todo 实际情况应该是去数据库中查是否已分配给用户
        //网关业务逻辑
        //1.先根据ak判断用户是否存在 查到sk
        //2.对比sk和用户传的加密后的sk是否一致
        User invokeUser = null;
        try {
            invokeUser =  innerUserService.getInvokeUser(accessKey);
        }catch (Exception e){
            log.error("getInvokeUser error",e);
        }

        if(invokeUser==null){
            return handlerNoAuth(response);
        }
//         模拟鉴权 ==是运算符 基本数据类型比较值 引用类型比较地址 equals 用于引用数据类型比较值 String是引用数据类型 需要比值
//        if(!"naer".equals(accessKey)){
//            return handlerNoAuth(response);
//        }
        if(Long.parseLong(nonce)>10000L){
            return handlerNoAuth(response);
        }
        //时间和当前时间不能超过5分钟
        if(System.currentTimeMillis()/1000-Long.parseLong(timestamp)>300){
            return handlerNoAuth(response);
        }
        String secretKey = invokeUser.getSecretKey();
        String serverSign = SignUtils.getSign(body, secretKey);
        if(sign ==null || !sign.equals(serverSign)){
            return handlerNoAuth(response);
        }
        long userId = invokeUser.getId();
        //4.请求的模拟接口是否存在
        //todo 从数据库中查询模拟接口是否存在 以及请求方法是否匹配 （还可以校验请求参数）
        InterfaceInfo interfaceInfo = null;
        try{
            interfaceInfo = innerInterfaceInfoService.getInterfaceInfo(path, method);
        }catch (Exception e){
            log.error("getInterfaceInfo error",e);
        }
        Long id = interfaceInfo.getId();
        if(interfaceInfo==null){
            return handlerNoAuth(response);
        }
        //5.请求的模拟接口调用次数是否超过限制
        if(innerUserInterfaceInfoService.selectCount(id,userId)){
            //超过限制次数
            return handlerInvokeError(response);
        }

        //7.请求转发，调用模拟接口-
//            Mono<Void> filter = chain.filter(exchange);
        //8.请求转发，调用模拟接口 + 响应日志
        return handleResponse(exchange, chain, id, invokeUser.getId());
//        log.info("响应状态码："+response.getStatusCode());

//        if(response.getStatusCode() == HttpStatus.OK){
//            //8.调用成功，接口调用次数+1
//            return handlerInvokeError(response);
//        } else{
//            //9.调用失败，返回一个规范的错误码
//            return chain.filter(exchange);
//        }
    };

    public Mono<Void> handleResponse(ServerWebExchange exchange, GatewayFilterChain chain,long interfaceInfo,long userId){
        try {
            ServerHttpResponse originalResponse = exchange.getResponse();
            DataBufferFactory bufferFactory = originalResponse.bufferFactory();
            HttpStatus statusCode = originalResponse.getStatusCode();
            if (statusCode != HttpStatus.OK) {
                return chain.filter(exchange);//降级处理返回数据
            }
            ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
                @Override
                public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                    if (body instanceof Flux) {
                        Flux<? extends DataBuffer> fluxBody = Flux.from(body);

                        return super.writeWith(fluxBody.buffer().map(dataBuffers -> {
                            //7.调用成功，接口调用次数+1 invokeCount
                            try{
                                innerUserInterfaceInfoService.invokeCount(interfaceInfo, userId);
                            }catch (Exception e){
                                log.error("invokeCount error",e);
                            }
                            // 合并多个流集合，解决返回体分段传输
                            DataBufferFactory dataBufferFactory = new DefaultDataBufferFactory();
                            DataBuffer buff = dataBufferFactory.join(dataBuffers);
                            byte[] content = new byte[buff.readableByteCount()];
                            buff.read(content);
                            DataBufferUtils.release(buff);//释放掉内存

                            //排除Excel导出，不是application/json不打印。若请求是上传图片则在最上面判断。
                            MediaType contentType = originalResponse.getHeaders().getContentType();
                            if (!MediaType.APPLICATION_JSON.isCompatibleWith(contentType)) {
                                return bufferFactory.wrap(content);
                            }

                            // 构建返回日志
                            String joinData = new String(content);
                            String result = modifyBody(joinData);
                            List<Object> rspArgs = new ArrayList<>();
                            rspArgs.add(originalResponse.getStatusCode().value());
                            rspArgs.add(exchange.getRequest().getURI());
                            rspArgs.add(result);
                            log.info("<-- {} {}\n{}", rspArgs.toArray());
                            log.info("响应结果："+result);

                            getDelegate().getHeaders().setContentLength(result.getBytes().length);
                            return bufferFactory.wrap(result.getBytes());
                        }));
                    } else {
                        log.error("<-- {} 响应code异常", getStatusCode());
                    }
                    return super.writeWith(body);
                }
            };
            return chain.filter(exchange.mutate().response(decoratedResponse).build());

        } catch (Exception e) {
            log.error("gateway log exception.\n" + e);
            return chain.filter(exchange);
        }

    }
    //返回统一的JSON日期数据 2024-02-23 11:00， null转空字符串
    private String modifyBody(String jsonStr){
        JSONObject json = JSON.parseObject(jsonStr, Feature.AllowISO8601DateFormat);
        JSONObject.DEFFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm";
        return JSONObject.toJSONString(json, (ValueFilter) (object, name, value) -> value == null ? "" : value, SerializerFeature.WriteDateUseDateFormat);
    }

    private Mono<Void> handlerNoAuth(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.FORBIDDEN);
        return response.setComplete();
    }

    private Mono<Void> handlerInvokeError(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        return response.setComplete();
    }

    public int getOrder(){
        return -1;
    }

}