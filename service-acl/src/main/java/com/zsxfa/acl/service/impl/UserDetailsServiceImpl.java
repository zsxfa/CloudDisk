package com.zsxfa.acl.service.impl;

import com.zsxfa.acl.pojo.entity.User;
import com.zsxfa.acl.service.AclPermissionService;
import com.zsxfa.acl.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.zsxfa.security.entity.SecurityUser;

import java.util.List;


/**
 * <p>
 * 自定义userDetailsService - 认证用户详情
 * </p>
 *
 * @author zsxfa
 */
@Service("userDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserService userService;
    @Autowired
    private AclPermissionService permissionService;

    /***
     * 根据账号获取用户信息
     * @param username:
     * @return: org.springframework.security.core.userdetails.UserDetails
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 从数据库中取出用户信息
        User user = userService.selectByUsername(username);

        // 判断用户是否存在
        if (null == user){
            throw new UsernameNotFoundException("用户名不存在！");
        }
        // 返回UserDetails实现类
        com.zsxfa.security.entity.User curUser = new com.zsxfa.security.entity.User();
        BeanUtils.copyProperties(user,curUser);

        List<String> authorities = permissionService.selectAclPermissionValueByUserId(String.valueOf(user.getUserid()));
        SecurityUser securityUser = new SecurityUser(curUser);
        securityUser.setPermissionValueList(authorities);
        return securityUser;
    }

}
