package com.naer.naerApiCommon.service;

import com.naer.naerApiCommon.model.entity.UserInterfaceInfo;

public interface InnerUserInterfaceInfoService {

    /**
     * 校验用户接口调用信息
     * @param userInterfaceInfo
     * @param add
     */
    void validUserInterfaceInfo(UserInterfaceInfo userInterfaceInfo, boolean add);

    /**
     * 调用接口统计
     * @param interfaceInfoId
     * @param userId
     * @return
     */
    boolean invokeCount(long interfaceInfoId, long userId);

    boolean selectCount(long interfaceInfoId, long userId);

}
