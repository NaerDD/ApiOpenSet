package com.naer.project.service.innerImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.naer.heartApiCommon.model.entity.UserInterfaceInfo;
import com.naer.heartApiCommon.service.InnerUserInterfaceInfoService;
import com.naer.project.common.ErrorCode;
import com.naer.project.exception.BusinessException;
import com.naer.project.service.UserInterfaceInfoService;
//import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @ClassName InnerUserInterfaceInfoServiceImpl
 * @Description TODO
 * @Author OTTO
 * @Date 2023/1/10 20:25
 */
//@DubboService
public class InnerUserInterfaceInfoServiceImpl implements InnerUserInterfaceInfoService {


    @Autowired
    private UserInterfaceInfoService userInterfaceInfoService;

    @Override
    public boolean invokeCount(long interfaceInfoId, long userId) {
        return userInterfaceInfoService.invokeCount(interfaceInfoId, userId);
    }

    /**
     * 判断剩余调用次数
     *
     * @param userId
     * @param interfaceInfoId
     * @return
     */
    @Override
    public boolean validLeftNum(Long userId, Long interfaceInfoId) {
        LambdaQueryWrapper<UserInterfaceInfo> lqw = new LambdaQueryWrapper<>();
        lqw.eq(UserInterfaceInfo::getUserId, userId)
                .eq(UserInterfaceInfo::getInterfaceInfoId, interfaceInfoId);
        UserInterfaceInfo userInterfaceInfo = userInterfaceInfoService.getOne(lqw);
        if (userInterfaceInfo == null || userInterfaceInfo.getLeftNum() <= 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        return true;
    }
}
