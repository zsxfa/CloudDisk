package com.zsxfa.cloud.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.zsxfa.cloud.core.pojo.entity.User;
import com.zsxfa.cloud.core.pojo.entity.UserLoginRecord;
import com.zsxfa.cloud.core.mapper.UserLoginRecordMapper;
import com.zsxfa.cloud.core.pojo.query.UserInfoQuery;
import com.zsxfa.cloud.core.service.UserLoginRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zsxfa
 * @since 2021-11-08
 */
@Service
public class UserLoginRecordServiceImpl extends ServiceImpl<UserLoginRecordMapper, UserLoginRecord> implements UserLoginRecordService {

    @Resource
    UserLoginRecordMapper userLoginRecordMapper;

    @Override
    public List<UserLoginRecord> userLoginList(Long page, Long limit, String userName) {
            return userLoginRecordMapper.userLoginList(page, limit, userName);


    }
}
