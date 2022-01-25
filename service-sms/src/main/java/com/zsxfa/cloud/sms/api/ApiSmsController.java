package com.zsxfa.cloud.sms.api;

import com.zsxfa.common.exception.Assert;
import com.zsxfa.common.result.R;
import com.zsxfa.common.result.ResponseEnum;
import com.zsxfa.common.util.RandomUtils;
import com.zsxfa.common.util.RegexValidateUtils;
import com.zsxfa.cloud.sms.client.CoreUserClient;
import com.zsxfa.cloud.sms.service.SmsService;
import com.zsxfa.cloud.sms.utils.SmsProperties;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/sms")
@Api(tags = "短信管理")
@Slf4j
public class ApiSmsController {

    @Resource
    private SmsService smsService;
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private CoreUserClient coreUserInfoClient;

    @ApiOperation("获取验证码")
    @GetMapping("/send/{mobile}")
    public R send(
            @ApiParam(value = "手机号", required = true)
            @PathVariable String mobile){

        //MOBILE_NULL_ERROR(-202, "手机号不能为空"),
        Assert.notEmpty(mobile, ResponseEnum.MOBILE_NULL_ERROR);
        //MOBILE_ERROR(-203, "手机号不正确"),
        Assert.isTrue(RegexValidateUtils.checkCellphone(mobile), ResponseEnum.MOBILE_ERROR);

        //判断手机号是否已经注册
        boolean result = coreUserInfoClient.checkMobile(mobile);
        log.info("result:"+result);
        Assert.isTrue(result == false, ResponseEnum.MOBILE_EXIST_ERROR);

        HashMap<String, Object> map = new HashMap<>();
        String code = RandomUtils.getFourBitRandom();
        map.put("code", code);
        smsService.send(mobile, SmsProperties.TEMPLATE_CODE,map);

        //将验证码存入redis
        redisTemplate.opsForValue().set("cloud:sms:code:" + mobile, code, 5, TimeUnit.MINUTES);

        return R.ok().message("短信发送成功");
    }
}
