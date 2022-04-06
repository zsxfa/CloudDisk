package com.zsxfa.acl.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zsxfa.acl.helper.MemuHelper;
import com.zsxfa.acl.helper.PermissionHelper;
import com.zsxfa.acl.mapper.AclPermissionMapper;
import com.zsxfa.acl.pojo.entity.AclPermission;
import com.zsxfa.acl.pojo.entity.AclRolePermission;
import com.zsxfa.acl.pojo.entity.User;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zsxfa.acl.service.AclPermissionService;
import com.zsxfa.acl.service.AclRolePermissionService;
import com.zsxfa.acl.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 权限 服务实现类
 * </p>
 *
 * @author zsxfa
 * @since 2022-01-15
 */
@Service
public class AclPermissionServiceImpl extends ServiceImpl<AclPermissionMapper, AclPermission> implements AclPermissionService {
    @Autowired
    private AclRolePermissionService roleAclPermissionService;

    @Autowired
    private UserService userService;

    //获取全部菜单
    @Override
    public List<AclPermission> queryAllMenu() {

        QueryWrapper<AclPermission> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("id");
        List<AclPermission> permissionList = baseMapper.selectList(wrapper);

        List<AclPermission> result = bulid(permissionList);

        return result;
    }

    //根据角色获取菜单
    @Override
    public List<AclPermission> selectAllMenu(String roleId) {
        List<AclPermission> allAclPermissionList = baseMapper.selectList(new QueryWrapper<AclPermission>().orderByAsc("CAST(id AS SIGNED)"));

        //根据角色id获取角色权限
        List<AclRolePermission> roleAclPermissionList = roleAclPermissionService.list(new QueryWrapper<AclRolePermission>().eq("role_id",roleId));

        for (int i = 0; i < allAclPermissionList.size(); i++) {
            AclPermission permission = allAclPermissionList.get(i);
            for (int m = 0; m < roleAclPermissionList.size(); m++) {
                AclRolePermission roleAclPermission = roleAclPermissionList.get(m);
                if(roleAclPermission.getPermissionId().equals(permission.getId())) {
                    permission.setSelect(true);
                }
            }
        }


        List<AclPermission> permissionList = bulid(allAclPermissionList);
        return permissionList;
    }

    //给角色分配权限
    @Override
    public void saveAclRolePermissionRealtionShip(String roleId, String[] permissionIds) {

        roleAclPermissionService.remove(new QueryWrapper<AclRolePermission>().eq("role_id", roleId));

        List<AclRolePermission> roleAclPermissionList = new ArrayList<>();
        for(String permissionId : permissionIds) {
            if(StringUtils.isEmpty(permissionId)){
                continue;
            }

            AclRolePermission roleAclPermission = new AclRolePermission();
            roleAclPermission.setRoleId(roleId);
            roleAclPermission.setPermissionId(permissionId);
            roleAclPermissionList.add(roleAclPermission);
        }
        roleAclPermissionService.saveBatch(roleAclPermissionList);
    }

    //递归删除菜单
    @Override
    public void removeChildById(String id) {
        List<String> idList = new ArrayList<>();
        this.selectChildListById(id, idList);

        idList.add(id);
        baseMapper.deleteBatchIds(idList);
    }

    //根据用户id获取用户菜单
    @Override
    public List<String> selectAclPermissionValueByUserId(String id) {

        List<String> selectAclPermissionValueList = null;
        if(this.isSysAdmin(id)) {
            //如果是系统管理员，获取所有权限
            selectAclPermissionValueList = baseMapper.selectAllPermissionValue();
        } else {
            selectAclPermissionValueList = baseMapper.selectPermissionValueByUserId(id);
        }
        return selectAclPermissionValueList;
    }

    @Override
    public List<JSONObject> selectAclPermissionByUserId(String userId) {
        List<AclPermission> selectAclPermissionList = null;
        if(this.isSysAdmin(userId)) {
            //如果是超级管理员，获取所有菜单
            selectAclPermissionList = baseMapper.selectList(null);
        } else {
            selectAclPermissionList = baseMapper.selectPermissionByUserId(userId);
        }
        List<AclPermission> permissionList = PermissionHelper.bulid(selectAclPermissionList);
        List<JSONObject> result = MemuHelper.bulid(permissionList);
        return result;
    }

    /**
     * 判断用户是否系统管理员
     * @param userId
     * @return
     */
    private boolean isSysAdmin(String userId) {
        User user = userService.getById(userId);

        if(null != user && "admin".equals(user.getUsername())) {
            return true;
        }
        return false;
    }

    /**
     *	递归获取子节点
     * @param id
     * @param idList
     */
    private void selectChildListById(String id, List<String> idList) {
        List<AclPermission> childList = baseMapper.selectList(new QueryWrapper<AclPermission>().eq("pid", id).select("id"));
        childList.stream().forEach(item -> {
            idList.add(item.getId());
            this.selectChildListById(item.getId(), idList);
        });
    }

    /**
     * 使用递归方法建菜单
     * @param treeNodes
     * @return
     */
    private static List<AclPermission> bulid(List<AclPermission> treeNodes) {
        List<AclPermission> trees = new ArrayList<>();
        for (AclPermission treeNode : treeNodes) {
            if ("0".equals(treeNode.getPid())) {
                treeNode.setLevel(1);
                trees.add(findChildren(treeNode,treeNodes));
            }
        }
        return trees;
    }

    /**
     * 递归查找子节点
     * @param treeNodes
     * @return
     */
    private static AclPermission findChildren(AclPermission treeNode,List<AclPermission> treeNodes) {
        treeNode.setChildren(new ArrayList<AclPermission>());

        for (AclPermission it : treeNodes) {
            if(treeNode.getId().equals(it.getPid())) {
                int level = treeNode.getLevel() + 1;
                it.setLevel(level);
                if (treeNode.getChildren() == null) {
                    treeNode.setChildren(new ArrayList<>());
                }
                treeNode.getChildren().add(findChildren(it,treeNodes));
            }
        }
        return treeNode;
    }


    //========================递归查询所有菜单================================================
    //获取全部菜单
    @Override
    public List<AclPermission> queryAllMenuGuli() {
        //1 查询菜单表所有数据
        QueryWrapper<AclPermission> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("id");
        List<AclPermission> permissionList = baseMapper.selectList(wrapper);
        //2 把查询所有菜单list集合按照要求进行封装
        List<AclPermission> resultList = bulidAclPermission(permissionList);
        return resultList;
    }

    //把返回所有菜单list集合进行封装的方法
    public static List<AclPermission> bulidAclPermission(List<AclPermission> permissionList) {

        //创建list集合，用于数据最终封装
        List<AclPermission> finalNode = new ArrayList<>();
        //把所有菜单list集合遍历，得到顶层菜单 pid=0菜单，设置level是1
        for(AclPermission permissionNode : permissionList) {
            //得到顶层菜单 pid=0菜单
            if("0".equals(permissionNode.getPid())) {
                //设置顶层菜单的level是1
                permissionNode.setLevel(1);
                //根据顶层菜单，向里面进行查询子菜单，封装到finalNode里面
                finalNode.add(selectChildren(permissionNode,permissionList));
            }
        }
        return finalNode;
    }

    public static AclPermission selectChildren(AclPermission permissionNode, List<AclPermission> permissionList) {
        //1 因为向一层菜单里面放二层菜单，二层里面还要放三层，把对象初始化
        permissionNode.setChildren(new ArrayList<AclPermission>());

        //2 遍历所有菜单list集合，进行判断比较，比较id和pid值是否相同
        for(AclPermission it : permissionList) {
            //判断 id和pid值是否相同
            if(permissionNode.getId().equals(it.getPid())) {
                //把父菜单的level值+1
                int level = permissionNode.getLevel()+1;
                it.setLevel(level);
                //如果children为空，进行初始化操作
                if(permissionNode.getChildren() == null) {
                    permissionNode.setChildren(new ArrayList<AclPermission>());
                }
                //把查询出来的子菜单放到父菜单里面
                permissionNode.getChildren().add(selectChildren(it,permissionList));
            }
        }
        return permissionNode;
    }

    //============递归删除菜单==================================
    @Override
    public void removeChildByIdGuli(String id) {
        //1 创建list集合，用于封装所有删除菜单id值
        List<String> idList = new ArrayList<>();
        //2 向idList集合设置删除菜单id
        this.selectAclPermissionChildById(id,idList);
        //把当前id封装到list里面
        idList.add(id);
        baseMapper.deleteBatchIds(idList);
    }

    @Override
    //2 根据当前菜单id，查询菜单里面子菜单id，封装到list集合
    public void selectAclPermissionChildById(String id, List<String> idList) {
        //查询菜单里面子菜单id
        QueryWrapper<AclPermission>  wrapper = new QueryWrapper<>();
        wrapper.eq("pid",id);
        wrapper.select("id");
        List<AclPermission> childIdList = baseMapper.selectList(wrapper);
        //把childIdList里面菜单id值获取出来，封装idList里面，做递归查询
        childIdList.stream().forEach(item -> {
            //封装idList里面
            idList.add(item.getId());
            //递归查询
            this.selectAclPermissionChildById(item.getId(),idList);
        });
    }

    //=========================给角色分配菜单=======================
    @Override
    public void saveAclRolePermissionRealtionShipGuli(String roleId, String[] permissionIds) {
        //roleId角色id
        //permissionId菜单id 数组形式
        //1 创建list集合，用于封装添加数据
        List<AclRolePermission> roleAclPermissionList = new ArrayList<>();
        //遍历所有菜单数组
        for(String perId : permissionIds) {
            //AclRolePermission对象
            AclRolePermission roleAclPermission = new AclRolePermission();
            roleAclPermission.setRoleId(roleId);
            roleAclPermission.setPermissionId(perId);
            //封装到list集合
            roleAclPermissionList.add(roleAclPermission);
        }
        //添加到角色菜单关系表
        roleAclPermissionService.saveBatch(roleAclPermissionList);
    }
}
