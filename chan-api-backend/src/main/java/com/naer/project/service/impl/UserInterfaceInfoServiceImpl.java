package com.naer.project.service.impl;


import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.naer.naerApiCommon.model.entity.UserInterfaceInfo;
import com.naer.project.common.ErrorCode;
import com.naer.project.exception.BusinessException;
import com.naer.project.mapper.UserInterfaceInfoMapper;
import com.naer.project.service.UserInterfaceInfoService;
import org.springframework.stereotype.Service;


@Service
public class UserInterfaceInfoServiceImpl extends ServiceImpl<UserInterfaceInfoMapper, UserInterfaceInfo>
        implements UserInterfaceInfoService {

    @Override
    public void validUserInterfaceInfo(UserInterfaceInfo userInterfaceInfo, boolean add) {
        if (userInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 创建时，所有参数必须非空
        if (add) {
            if (userInterfaceInfo.getInterfaceInfoId() <= 0 || userInterfaceInfo.getUserId() <= 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口或用户不存在");
            }
        }
        if (userInterfaceInfo.getLeftNum() < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "剩余次数不能小于0");
        }

    }

    /**
     * 调用接口统计
     *
     * @param interfaceInfoId
     * @param userId
     * @return
     */
    @Override
    public boolean invokeCount(long interfaceInfoId, long userId) {
        //先判断是否有这条记录 如果有就执行 没有就新增
        if (interfaceInfoId <= 0 || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UpdateWrapper<UserInterfaceInfo> luw = new UpdateWrapper<>();
        luw.eq("interfaceInfoId", interfaceInfoId);
        luw.eq("userId", userId);
        luw.setSql(" leftNum = leftNum - 1 , totalNum = totalNum + 1 ");
        return this.update(luw);
    }
}