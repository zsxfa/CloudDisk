package com.zsxfa.cloud.core.mapper;

import com.zsxfa.cloud.core.pojo.entity.User;
import com.zsxfa.cloud.core.pojo.entity.UserLoginRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zsxfa.cloud.core.pojo.query.UserInfoQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author zsxfa
 */
public interface UserLoginRecordMapper extends BaseMapper<UserLoginRecord> {

    //获取登录日志
    List<UserLoginRecord> userLoginList(Long page, Long limit,  String userName);

}
