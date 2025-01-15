package com.naer.project.mapper;



import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.naer.naerApiCommon.model.entity.UserInterfaceInfo;

import java.util.List;

/**
* @Entity com.yupi.project.model.domain.UserInterfaceInfo
*/
public interface UserInterfaceInfoMapper extends BaseMapper<UserInterfaceInfo> {


    List<UserInterfaceInfo> listTopInterfaceInfoInvoke(int limit);
}




