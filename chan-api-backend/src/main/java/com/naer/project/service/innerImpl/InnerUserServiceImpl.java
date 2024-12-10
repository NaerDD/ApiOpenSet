package com.naer.project.service.innerImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.naer.naerApiCommon.model.entity.User;
import com.naer.naerApiCommon.service.InnerUserService;
import com.naer.project.common.ErrorCode;
import com.naer.project.exception.BusinessException;
import com.naer.project.mapper.UserMapper;
import org.apache.commons.lang3.StringUtils;
//import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @ClassName InnerUserServiceImpl
 * @Description TODO
 * @Author OTTO
 * @Date 2023/1/10 19:59
 */
//@DubboService
public class InnerUserServiceImpl implements InnerUserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User getInvokeUser(String accessKey) {
        if(StringUtils.isAnyBlank(accessKey)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper<>();
        lqw.eq(User::getAccessKey,accessKey);
        return userMapper.selectOne(lqw);
    }
}
