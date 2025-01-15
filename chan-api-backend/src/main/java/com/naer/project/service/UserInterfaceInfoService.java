package com.naer.project.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.naer.naerApiCommon.model.entity.UserInterfaceInfo;

public interface UserInterfaceInfoService extends IService<UserInterfaceInfo> {

    void validUserInterfaceInfo(UserInterfaceInfo interfaceInfo, boolean add);

    boolean invokeCount(long interfaceInfoId, long userId);
}
