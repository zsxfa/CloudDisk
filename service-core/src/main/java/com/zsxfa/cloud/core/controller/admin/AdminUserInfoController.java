package com.zsxfa.cloud.core.controller.admin;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zsxfa.cloud.core.pojo.entity.User;
import com.zsxfa.cloud.core.pojo.entity.UserInfo;
import com.zsxfa.cloud.core.pojo.entity.UserLoginRecord;
import com.zsxfa.cloud.core.pojo.query.UserInfoQuery;
import com.zsxfa.cloud.core.service.UserFileService;
import com.zsxfa.cloud.core.service.UserService;
import com.zsxfa.common.result.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author zsxfa
 */
@Api(tags = "用户管理")
@RestController
@RequestMapping("/admin/core/user")
public class AdminUserInfoController {
    @Resource
    private UserService userService;

    @ApiOperation("获取用户路由列表")
    @GetMapping("/routes")
    public R getMenu(){
//        List<AuthSysMenu> menuList = this.authService.getMenu();
//        return R.ok().data("menuList",menuList);
        return null;
    }

    @ApiOperation("获取用户分页列表")
    @GetMapping("/list/{page}/{limit}")
    public R listPage(
            @ApiParam(value = "当前页码", required = true)
            @PathVariable Long page,

            @ApiParam(value = "每页记录数", required = true)
            @PathVariable Long limit,

            @ApiParam(value = "用户电话", required = false)
            @RequestParam(value="telephone",required=false) String telephone,

            @ApiParam(value = "用户姓名", required = false)
            @RequestParam(value="userName",required=false) String userName) {

        Page<User> list = userService.searchUserList(page-1, limit, userName, telephone);

        Map<String, Object> map = new HashMap<>();
        map.put("list", list.getRecords());
        map.put("total", list.getTotal());

        return R.ok().data(map);
    }
}

