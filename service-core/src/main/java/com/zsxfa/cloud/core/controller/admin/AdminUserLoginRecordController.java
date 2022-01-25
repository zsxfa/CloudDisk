package com.zsxfa.cloud.core.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zsxfa.cloud.core.pojo.entity.User;
import com.zsxfa.cloud.core.pojo.entity.UserFile;
import com.zsxfa.cloud.core.pojo.entity.UserLoginRecord;
import com.zsxfa.cloud.core.pojo.query.UserInfoQuery;
import com.zsxfa.cloud.core.service.UserLoginRecordService;
import com.zsxfa.common.result.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(tags = "用户登录日志接口")
@RestController
@RequestMapping("/admin/core/userLoginRecord")
@Slf4j
public class AdminUserLoginRecordController {

    @Resource
    private UserLoginRecordService userLoginRecordService;

    @ApiOperation("获取会员登录日志列表")
    @GetMapping("/list/{page}/{limit}")
    public R loginList(
            @ApiParam(value = "当前页码", required = true)
            @PathVariable Long page,

            @ApiParam(value = "每页记录数", required = true)
            @PathVariable Long limit,

            @ApiParam(value = "用户姓名", required = false)
            @RequestParam(value="userName",required=false) String userName) {

        if (limit == 0 || page == 0) {
            page = 0L;
            limit = 10L;
        } else {
            page = (page - 1) * limit;
        }
        List<UserLoginRecord> userLoginRecordList = null;
        LambdaQueryWrapper<UserLoginRecord> userLogLambdaQueryWrapper = new LambdaQueryWrapper<>();
        if(StringUtils.isBlank(userName)){
            userLoginRecordList = userLoginRecordService.userLoginList(page, limit, null);
        }else{
            userLoginRecordList = userLoginRecordService.userLoginList(page, limit, userName);
            userLogLambdaQueryWrapper.like(UserLoginRecord::getUserName, userName);
        }
        int total = userLoginRecordService.count(userLogLambdaQueryWrapper);

        Map<String, Object> map = new HashMap<>();
        map.put("total", total);
        map.put("list", userLoginRecordList);

        return R.ok().data(map);
    }
}
