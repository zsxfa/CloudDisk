package com.zsxfa.cloud.core.controller.api;


import com.zsxfa.cloud.base.util.JwtUtils;
import com.zsxfa.cloud.core.pojo.entity.User;
import com.zsxfa.cloud.core.pojo.vo.LoginVO;
import com.zsxfa.cloud.core.pojo.vo.RegisterVO;
import com.zsxfa.cloud.core.pojo.vo.UserVO;
import com.zsxfa.cloud.core.service.UserService;
import com.zsxfa.common.exception.Assert;
import com.zsxfa.common.result.R;
import com.zsxfa.common.result.ResponseEnum;
import com.zsxfa.common.util.RegexValidateUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author zsxfa
 */
@Api(tags = "用户管理")
@RestController
@RequestMapping("/api/core/user")
public class UserController {

    @Resource
    private UserService userService;
    @Resource
    RedisTemplate redisTemplate;

    @ApiOperation(value = "会员注册")
    @PostMapping("/register")
    public R register(@RequestBody RegisterVO registerVO){

        String mobile = registerVO.getTelephone();
        String password = registerVO.getPassword();
        String code = registerVO.getCode();

        //MOBILE_NULL_ERROR(-202, "手机号不能为空"),
        Assert.notEmpty(mobile, ResponseEnum.MOBILE_NULL_ERROR);
        //MOBILE_ERROR(-203, "手机号不正确"),
        Assert.isTrue(RegexValidateUtils.checkCellphone(mobile), ResponseEnum.MOBILE_ERROR);
        //PASSWORD_NULL_ERROR(-204, "密码不能为空"),
        //CODE_NULL_ERROR(-205, "验证码不能为空"),
        Assert.notEmpty(code, ResponseEnum.CODE_NULL_ERROR);
        Assert.notEmpty(password, ResponseEnum.PASSWORD_NULL_ERROR);

        //校验验证码是否成功
        String codeGen = (String) redisTemplate.opsForValue().get("cloud:sms:code:"+mobile);
        //CODE_ERROR(-206, "验证码不正确"),
        Assert.equals(code, codeGen, ResponseEnum.CODE_ERROR);

        //注册
        userService.register(registerVO);
        return R.ok().message("注册成功");
    }

    @ApiOperation("用户登录")
    @PostMapping("/login")
    @ResponseBody
    public R login(@RequestBody LoginVO loginVO, HttpServletRequest request) {
        String telephone = loginVO.getTelephone();
        String password = loginVO.getPassword();

        Assert.notEmpty(telephone, ResponseEnum.MOBILE_NULL_ERROR);
        Assert.notEmpty(password, ResponseEnum.PASSWORD_NULL_ERROR);

        String ip = request.getRemoteAddr();
        UserVO userVO = userService.login(loginVO, ip);

        return R.ok().data("user", userVO);
    }


    @ApiOperation("检查用户登录信息")
    @GetMapping("/checkuserlogininfo")
    @ResponseBody
    public R checkUserLoginInfo(@RequestHeader("token") String token) {

        boolean result = JwtUtils.checkToken(token);

        if(result){
            User userByToken = userService.getUserByToken(token);
            return R.ok().data("user",userByToken);
        }else{
            //LOGIN_AUTH_ERROR(-211, "未登录"),
            return R.setResult(ResponseEnum.LOGIN_AUTH_ERROR);
        }

    }
}

