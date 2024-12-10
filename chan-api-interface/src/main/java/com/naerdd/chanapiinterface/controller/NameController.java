package com.naerdd.chanapiinterface.controller;


import com.naer.chanapiclientsdk.model.User;
import com.naer.chanapiclientsdk.utils.SignUtils;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;


/**
 * 名称 API
 */
@RestController
@RequestMapping("/name")
public class NameController {

    @GetMapping("/")
   public String getNameByGet(String name){
        return "GET 你的名字是："+name;
    }

    //@RequestParam xx?=xx 参数  Param 的形式
    @PostMapping("/")
    public String getNameBypost(@RequestParam String name){
        return "POST 你的名字是："+name;
    }

    //@RequestBody  对象的形式  Body
    @PostMapping("/user")
    public String getUserNameByPost(@RequestBody User user, HttpServletRequest request){
        String accessKey = request.getHeader("accessKey");
        String nonce = request.getHeader("nonce");
        String timestamp = request.getHeader("timestamp");
        String sign = request.getHeader("sign");
        String body = request.getHeader("body");

//         模拟鉴权 ==是运算符 基本数据类型比较值 引用类型比较地址 equals 用于引用数据类型比较值 String是引用数据类型 需要比值
        if(!accessKey.equals("naer")){
            throw new RuntimeException("无权限");
        }
        if(Long.parseLong(nonce)>10000){
            throw new RuntimeException("无权限");
        }
        //时间和当前时间不能超过5分钟
        if(System.currentTimeMillis()/1000-Long.parseLong(timestamp)>300){
            throw new RuntimeException("无权限");
        }
        String serverSign = SignUtils.getSign(body, "1234");
        if(!sign.equals(serverSign)){
            throw new RuntimeException("无权限");
        }
        String result = "POST 用户名字是" + user.getUsername();
        return result;
    }
}
