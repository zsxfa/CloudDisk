package com.zsxfa.cloud.core.pojo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description="用户信息对象")
public class UserVO {
    @ApiModelProperty(value = "用户姓名")
    private String username;

    @ApiModelProperty(value = "头像")
    private String headImg;

    @ApiModelProperty(value = "手机号")
    private String mobile;

    @ApiModelProperty(value = "1：管理员 2：用户")
    private Integer role;

    @ApiModelProperty(value = "JWT访问令牌")
    private String token;
}
