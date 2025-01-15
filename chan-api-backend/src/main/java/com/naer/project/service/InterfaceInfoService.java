package com.naer.project.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.naer.naerApiCommon.model.entity.InterfaceInfo;

public interface InterfaceInfoService extends IService<InterfaceInfo> {

    void validInterfaceInfo(InterfaceInfo interfaceInfo, boolean add);
}
