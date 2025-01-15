package com.naer.naerApiCommon.service;

import com.naer.naerApiCommon.model.entity.InterfaceInfo;

public interface InnerInterfaceInfoService  {

    /**
     2.从数据库中查询模拟接口是否存在(请求路径、请求方法、请求参数,返回接口信息 为空表示不存在)
     */
    InterfaceInfo getInterfaceInfo(String url,String method);
}
