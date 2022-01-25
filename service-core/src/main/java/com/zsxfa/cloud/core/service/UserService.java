package com.zsxfa.cloud.core.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zsxfa.cloud.core.pojo.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zsxfa.cloud.core.pojo.entity.UserLoginRecord;
import com.zsxfa.cloud.core.pojo.query.UserInfoQuery;
import com.zsxfa.cloud.core.pojo.vo.LoginVO;
import com.zsxfa.cloud.core.pojo.vo.RegisterVO;
import com.zsxfa.cloud.core.pojo.vo.UserVO;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zsxfa
 */
public interface UserService extends IService<User> {


    void register(RegisterVO registerVO);

    UserVO login(LoginVO loginVO, String ip);

    User getUserByToken(String token);

    //查找用户
    Page<User> searchUserList(Long page, Long limit, String userName, String telephone);

}
