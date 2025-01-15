package com.naer.project.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.naer.naerApiCommon.model.entity.InterfaceInfo;
import com.naer.naerApiCommon.model.entity.UserInterfaceInfo;
import com.naer.project.annotation.AuthCheck;
import com.naer.project.common.BaseResponse;
import com.naer.project.common.ErrorCode;
import com.naer.project.common.ResultUtils;
import com.naer.project.exception.BusinessException;
import com.naer.project.mapper.UserInterfaceInfoMapper;
import com.naer.project.model.vo.InterfaceVo;
import com.naer.project.service.InterfaceInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 分析控制器
 */
@RestController
@RequestMapping("/analysis")
@Slf4j
public class AnalysisController {

    @Resource
    private  UserInterfaceInfoMapper userInterfaceInfoMapper;

    @Resource
    private InterfaceInfoService interfaceInfoService;

    @GetMapping("/top/interface/invoke")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<List<InterfaceVo>> ListTopInvokeInterfaceInfo() {
        //把两个表关联起来
        List<UserInterfaceInfo> userInterfaceInfoList = userInterfaceInfoMapper.listTopInterfaceInfoInvoke(4);

        // 1. 按 interfaceInfoId 分组
        Map<Long, List<UserInterfaceInfo>> interfaceInfoIdObjMap = userInterfaceInfoList.stream()
                        .collect(Collectors.groupingBy(UserInterfaceInfo::getInterfaceInfoId));

        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id", interfaceInfoIdObjMap.keySet());
        List<InterfaceInfo> list = interfaceInfoService.list(queryWrapper);
        // 判空
        if(CollectionUtils.isEmpty(list)){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        // 2. 聚合
        List<InterfaceVo> collect = list.stream().map(interfaceInfo -> {
            InterfaceVo interfaceVo = new InterfaceVo();
            BeanUtils.copyProperties(interfaceInfo, interfaceVo);
            int totalNum = interfaceInfoIdObjMap.get(interfaceInfo.getId()).get(0).getTotalNum();
            interfaceVo.setTotalNum(totalNum);
            return interfaceVo;
        }).collect(Collectors.toList());
        return ResultUtils.success(collect);
    };
}
