package com.naer.project.service.impl.inner;

import com.naer.naerApiCommon.model.entity.InterfaceInfo;
import com.naer.naerApiCommon.service.InnerInterfaceInfoService;
import com.naer.project.service.InterfaceInfoService;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

@DubboService
public class InnerInterfaceInfoServiceImpl implements InnerInterfaceInfoService {
    @Resource
    private InterfaceInfoService interfaceInfoService;

    @Override
    public InterfaceInfo getInterfaceInfo(String url, String method) {
        if(StringUtils.isAnyBlank(url, method)){
            throw new RuntimeException("参数为空");
        }
        return interfaceInfoService.query()
                .eq("url", url)
                .eq("method", method)
                .one();
    }

}
