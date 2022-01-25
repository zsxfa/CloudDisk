package com.zsxfa.cloud.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zsxfa.cloud.core.pojo.entity.UserLoginRecord;
import com.zsxfa.cloud.core.pojo.query.UserInfoQuery;

import java.util.List;

/**
 * <p>
 * 用户登录记录表 服务类
 * </p>
 *
 * @author Jachin
 */
public interface UserLoginRecordService extends IService<UserLoginRecord> {

    //获取登录日志
    List<UserLoginRecord> userLoginList(Long page, Long limit, String userName);




}
