package com.naer.chanapiclientsdk.client;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.naer.chanapiclientsdk.model.User;

import java.util.HashMap;
import java.util.Map;

import static com.naer.chanapiclientsdk.utils.SignUtils.getSign;

/**
 * 调用第三方接口的客户端
 *
 */

public class NaerApiClient {

    private static final String GATEWAY_HOST = "http://localhost:8090";

    private String accessKey;

    private String secretKey;

    public NaerApiClient(String accessKey, String secretKey) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }

    public String getNameByGet(String name){
        //可以单独传入http参数，这样参数会自动做URL编码，拼接在URL中
        HashMap<String,Object> params = new HashMap<>();
        params.put("name",name);
        String result = HttpUtil.get(GATEWAY_HOST+"/api/name/",params);
        System.out.println(result);
        return result;
    }

    public String getNameBypost(String name){
        //可以单独传入http参数，这样参数会自动做URL编码，拼接在URL中
        HashMap<String,Object> params = new HashMap<>();
        params.put("name",name);
        String result = HttpUtil.post(GATEWAY_HOST+"/api/name/",params);
        System.out.println(result);
        return result;
    }


    private Map<String,String> getHeaderMap(String body){
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("accessKey",accessKey);
//        hashMap.put("secretKey",secretKey);
        hashMap.put("nonce", RandomUtil.randomNumbers(4));
        hashMap.put("body",body);
        hashMap.put("timestamp",String.valueOf(System.currentTimeMillis()/1000));
        hashMap.put("sign",getSign(body,secretKey));
        return hashMap;
    }

    //1.前端 填写 参数 chan-api-backend模拟客户端发送请求
    //2.发送到http://localhost:8090/api/name/user” 交给api网关
    //3.网络逻辑处理之后 请求转发，调用模拟接口
    //4.Nacos远程调用 backend去实现 common公共类中定义接口 然后gateway和backend 服务提供者和使用者都引入common gateway就可以直接使用
    public String getUserNameByPost(User user){
        String json = JSONUtil.toJsonStr(user);
        HttpResponse httpResponse =  HttpRequest.post(GATEWAY_HOST+"/api/name/user")
                .addHeaders(getHeaderMap(json))
                .body(json)
                .execute();
        System.out.println(httpResponse.getStatus());
        String result = httpResponse.body();
        System.out.println(result);
        return result;
    }



}
