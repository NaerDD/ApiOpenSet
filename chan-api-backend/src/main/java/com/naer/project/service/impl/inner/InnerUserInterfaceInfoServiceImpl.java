package com.naer.project.service.impl.inner;



import com.naer.naerApiCommon.model.entity.UserInterfaceInfo;
import com.naer.naerApiCommon.service.InnerUserInterfaceInfoService;
import com.naer.project.service.UserInterfaceInfoService;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

@DubboService
public class InnerUserInterfaceInfoServiceImpl implements InnerUserInterfaceInfoService {

    @Resource
    private UserInterfaceInfoService userInterfaceInfoService;

    @Override
    public void validUserInterfaceInfo(UserInterfaceInfo userInterfaceInfo, boolean add) {

    }

    @Override
    public boolean invokeCount(long interfaceInfoId, long userId) {
        return  userInterfaceInfoService.invokeCount(interfaceInfoId,userId);
    }

    // 检查用户剩余调用次数
    @Override
    public boolean selectCount(long interfaceInfoId, long userId) {
        UserInterfaceInfo info = userInterfaceInfoService.getById(userId);
        Integer num = info.getLeftNum();
        if (num <= 0) {
            return true;
        }else{
            return false;
        }
    }
}