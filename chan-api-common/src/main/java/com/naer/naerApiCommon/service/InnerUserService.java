package com.naer.naerApiCommon.service;

import com.naer.naerApiCommon.model.entity.User;

/**
 * 用户服务
 *
 * @author yupi
 */
public interface InnerUserService {

    User getInvokeUser(String accessKey);

}
