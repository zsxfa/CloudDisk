package com.zsxfa.acl.service;

import com.alibaba.fastjson.JSONObject;
import com.zsxfa.acl.pojo.entity.AclPermission;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 权限 服务类
 * </p>
 *
 * @author zsxfa
 */
public interface AclPermissionService extends IService<AclPermission> {

    //获取全部菜单
    List<AclPermission> queryAllMenu();

    //根据角色获取菜单
    List<AclPermission> selectAllMenu(String roleId);

    //给角色分配权限
    void saveAclRolePermissionRealtionShip(String roleId, String[] permissionIds);

    //递归删除菜单
    void removeChildById(String id);

    //根据用户id获取用户菜单
    List<String> selectAclPermissionValueByUserId(String id);

    List<JSONObject> selectAclPermissionByUserId(String userId);

    //获取全部菜单
    List<AclPermission> queryAllMenuGuli();

    //递归删除菜单
    void removeChildByIdGuli(String id);

    //2 根据当前菜单id，查询菜单里面子菜单id，封装到list集合

    void selectAclPermissionChildById(String id, List<String> idList);

    //=========================给角色分配菜单=======================
    void saveAclRolePermissionRealtionShipGuli(String roleId, String[] permissionIds);
}
