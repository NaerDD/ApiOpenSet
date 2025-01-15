package com.naer.project.model.vo;

import com.naer.naerApiCommon.model.entity.InterfaceInfo;
import lombok.Data;

/**
 * interface包装类
 */
@Data
public class InterfaceVo extends InterfaceInfo {

    /*调用次数*/
    private int totalNum;

    private static final long serialVersionUID = 1L;
}
