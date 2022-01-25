package com.zsxfa.acl.mapper;

import com.zsxfa.acl.pojo.entity.AclPermission;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * 权限 Mapper 接口
 * </p>
 *
 * @author zsxfa
 */
public interface AclPermissionMapper extends BaseMapper<AclPermission> {

    List<String> selectPermissionValueByUserId(String id);

    List<String> selectAllPermissionValue();

    List<AclPermission> selectPermissionByUserId(String userId);


}
