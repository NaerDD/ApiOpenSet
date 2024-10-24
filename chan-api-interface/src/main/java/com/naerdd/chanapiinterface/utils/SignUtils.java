package com.naerdd.chanapiinterface.utils;

import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.Digester;

/**
 * 签名工具
 */
public class SignUtils {
    /**
     * 生成签名
     * @param body
     * @param secretKey
     * @return
     */
    public static String getSign(String body, String secretKey){
        Digester sha256 = new Digester(DigestAlgorithm.SHA256);
        String content = body + "." + secretKey;
        //单向加密  不可逆
        return sha256.digestHex(content);
    }
}
