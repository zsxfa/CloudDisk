package com.zsxfa.cloud.core.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zsxfa.cloud.core.pojo.entity.AclUserRole;
import com.zsxfa.cloud.core.mapper.AclUserRoleMapper;
import com.zsxfa.cloud.base.util.JwtUtils;
import com.zsxfa.cloud.core.mapper.UserLoginRecordMapper;
import com.zsxfa.cloud.core.pojo.entity.User;
import com.zsxfa.cloud.core.mapper.UserMapper;
import com.zsxfa.cloud.core.pojo.entity.UserLoginRecord;
import com.zsxfa.cloud.core.pojo.query.UserInfoQuery;
import com.zsxfa.cloud.core.pojo.vo.LoginVO;
import com.zsxfa.cloud.core.pojo.vo.RegisterVO;
import com.zsxfa.cloud.core.pojo.vo.UserVO;
import com.zsxfa.cloud.core.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zsxfa.common.exception.Assert;
import com.zsxfa.common.result.ResponseEnum;
import com.zsxfa.common.util.DateUtil;
import com.zsxfa.common.util.MD5;
import io.jsonwebtoken.Claims;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zsxfa
 * @since 2021-11-13
 */
@Service
@Transactional(rollbackFor=Exception.class)
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Resource
    private UserMapper userMapper;
    @Resource
    private UserLoginRecordMapper userLoginRecordMapper;
    @Resource
    private AclUserRoleMapper userRoleMapper;


    @ApiOperation("用户注册")
    @Override
    public void register(RegisterVO registerVO) {
        //判断用户是否被注册
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("telephone", registerVO.getTelephone());
        Integer count = baseMapper.selectCount(queryWrapper);
        //MOBILE_EXIST_ERROR(-207, "手机号已被注册"),
        Assert.isTrue(count == 0, ResponseEnum.MOBILE_EXIST_ERROR);

        //插入用户基本信息
        User user = new User();
        user.setUsername(registerVO.getUsername());
        user.setTelephone(registerVO.getTelephone());
        user.setPassword(MD5.encrypt(registerVO.getPassword()));

        baseMapper.insert(user);

        System.out.println("user的id是："+user.getUserId());
        System.out.println("-----------------------------");
        AclUserRole aclUserRole = new AclUserRole();
        aclUserRole.setUserId(String.valueOf(user.getUserId()));
        aclUserRole.setRoleId("2");
        userRoleMapper.insert(aclUserRole);
    }

    @ApiOperation("用户登录")
    @Transactional( rollbackFor = {Exception.class})
    @Override
    public UserVO login(LoginVO loginVO, String ip) {
        String telephone = loginVO.getTelephone();
        String password = loginVO.getPassword();
        password = MD5.encrypt(password);

        //获取会员
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("telephone", telephone);
        queryWrapper.eq("password", password);
        User user = baseMapper.selectOne(queryWrapper);
        //用户不存在
        //LOGIN_MOBILE_ERROR(-208, "用户不存在"),
        Assert.notNull(user, ResponseEnum.LOGIN_MOBILE_ERROR);
        //校验密码
        //LOGIN_PASSWORD_ERROR(-209, "密码不正确"),
        Assert.equals(password, user.getPassword(), ResponseEnum.LOGIN_PASSWORD_ERROR);
        //记录登录日志
        UserLoginRecord userLoginRecord = new UserLoginRecord();
        userLoginRecord.setId(user.getUserId());
        userLoginRecord.setUserName(user.getUsername());
        userLoginRecord.setIp(ip);
        userLoginRecordMapper.insert(userLoginRecord);

        //生成token
        String token = JwtUtils.createToken(user.getUserId(), user.getUsername());

        UserVO userVO = new UserVO();
        userVO.setToken(token);
        userVO.setUsername(user.getUsername());
        userVO.setMobile(user.getTelephone());
        return userVO;
    }

    /**
     * 通过token获取用户信息
     */
    @Override
    public User getUserByToken(String token) {
        Long id = JwtUtils.getUserId(token);
        User user = findUserByUserId(id);
        return user;
    }

    @Override
    public Page<User> searchUserList(Long page, Long limit, String userName, String telephone) {
        LambdaQueryWrapper<User> userLogLambdaQueryWrapper = new LambdaQueryWrapper<>();
        if(StringUtils.isNotBlank(userName) && StringUtils.isNotBlank(telephone)){
            userLogLambdaQueryWrapper.like(User::getTelephone, telephone);
            userLogLambdaQueryWrapper.like(User::getUsername, userName);

        }else if(StringUtils.isBlank(telephone) && StringUtils.isNotBlank(userName)){
            userLogLambdaQueryWrapper.like(User::getUsername, userName);
        }else if(StringUtils.isBlank(userName) && StringUtils.isNotBlank(telephone)){
            userLogLambdaQueryWrapper.like(User::getTelephone, telephone);
        }

        Page<User> p = new Page<>(page,limit);
        return userMapper.selectPage(p, userLogLambdaQueryWrapper);
    }

    /**
     * 通过用户id获取用户信息
     */
    public User findUserByUserId(Long userId) {
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(User::getUserId, userId);
        return userMapper.selectOne(lambdaQueryWrapper);

    }

}
