package com.naer.project.service.innerImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.naer.heartApiCommon.model.entity.InterfaceInfo;
import com.naer.heartApiCommon.service.InnerInterfaceInfoService;
import com.naer.project.common.ErrorCode;
import com.naer.project.exception.BusinessException;
import com.naer.project.mapper.InterfaceInfoMapper;
import org.apache.commons.lang3.StringUtils;
//import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @ClassName InnerInterfaceInfoServiceImpl
 * @Description TODO
 * @Author OTTO
 * @Date 2023/1/10 20:01
 */

//@DubboService
public class InnerInterfaceInfoServiceImpl implements InnerInterfaceInfoService {

    @Autowired
    private InterfaceInfoMapper interfaceInfoMapper;

    @Override
    public InterfaceInfo getInterfaceInfo(String url,String method) {
        if(StringUtils.isAnyBlank(url,method)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        LambdaQueryWrapper<InterfaceInfo> lqw = new LambdaQueryWrapper<>();
        lqw.eq(InterfaceInfo::getUrl,url)
                .eq(InterfaceInfo::getMethod,method);
        InterfaceInfo interfaceInfo = interfaceInfoMapper.selectOne(lqw);
        return interfaceInfo;
    }
}
