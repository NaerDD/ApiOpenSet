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
        String result = HttpUtil.get("http://localhost:8123/api/name/",params);
        System.out.println(result);
        return result;
    }

    public String getNameBypost(String name){
        //可以单独传入http参数，这样参数会自动做URL编码，拼接在URL中
        HashMap<String,Object> params = new HashMap<>();
        params.put("name",name);
        String result = HttpUtil.post("http://localhost:8123/api/name/",params);
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

    public String getUserNameByPost(User user){
        String json = JSONUtil.toJsonStr(user);
        HttpResponse httpResponse =  HttpRequest.post("http://localhost:8123/api/name/user")
                .addHeaders(getHeaderMap(json))
                .body(json)
                .execute();
        System.out.println(httpResponse.getStatus());
        String result = httpResponse.body();
        System.out.println(result);
        return result;
    }
}
