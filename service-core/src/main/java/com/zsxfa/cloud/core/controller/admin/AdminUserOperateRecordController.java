package com.zsxfa.cloud.core.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zsxfa.cloud.core.pojo.dto.UserOperateLog;
import com.zsxfa.cloud.core.pojo.entity.Operationlog;
import com.zsxfa.cloud.core.pojo.entity.User;
import com.zsxfa.cloud.core.pojo.entity.UserLoginRecord;
import com.zsxfa.cloud.core.service.OperationlogService;
import com.zsxfa.cloud.core.service.UserLoginRecordService;
import com.zsxfa.cloud.core.service.UserService;
import com.zsxfa.common.result.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(tags = "用户操作日志接口")
@RestController
@RequestMapping("/admin/core/userOperateRecord")
@Slf4j
public class AdminUserOperateRecordController {

    @Resource
    private UserLoginRecordService userLoginRecordService;
    @Resource
    private OperationlogService operationlogService;
    @Resource
    private UserService userService;

    @ApiOperation("获取会员操作日志列表")
    @GetMapping("/list/{page}/{limit}")
    public R operateList(
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

        List<Operationlog> userOperateRecordList = null;
        LambdaQueryWrapper<Operationlog> userLogLambdaQueryWrapper = new LambdaQueryWrapper<>();
        if(StringUtils.isBlank(userName)){
            userOperateRecordList = operationlogService.userOperateList(page, limit, null);
        }else{
            //先通过username查到userid
            LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
            userLambdaQueryWrapper.eq(User::getUsername,userName);
            User user = userService.getOne(userLambdaQueryWrapper);
            if(user != null){
                userOperateRecordList = operationlogService.userOperateList(page, limit, user.getUserId());
                userLogLambdaQueryWrapper.eq(Operationlog::getUserid, user.getUserId());
            }else{
                userOperateRecordList = operationlogService.userOperateList(page, limit, null);
            }
        }
        List<UserOperateLog> userOperateRecordDTOList = new ArrayList<>();
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        for (Operationlog list : userOperateRecordList){
            UserOperateLog userOperateLog = new UserOperateLog();
            userLambdaQueryWrapper.eq(User::getUserId,list.getUserid());
            User user = userService.getOne(userLambdaQueryWrapper);
            BeanUtils.copyProperties(list, userOperateLog);
            userOperateLog.setUsername(user.getUsername());
            userOperateRecordDTOList.add(userOperateLog);
        }

        int total = operationlogService.count(userLogLambdaQueryWrapper);

        Map<String, Object> map = new HashMap<>();
        map.put("total", total);
        map.put("list", userOperateRecordDTOList);

        return R.ok().data(map);
    }
}
