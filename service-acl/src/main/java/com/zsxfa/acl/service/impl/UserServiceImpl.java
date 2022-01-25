package com.zsxfa.acl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zsxfa.acl.mapper.UserMapper;
import com.zsxfa.acl.pojo.entity.User;
import com.zsxfa.acl.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zsxfa
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Override
    public User selectByUsername(String username) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        return baseMapper.selectOne(wrapper);
    }
}
