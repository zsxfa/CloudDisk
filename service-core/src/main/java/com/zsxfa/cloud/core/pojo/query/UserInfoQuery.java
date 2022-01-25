package com.zsxfa.cloud.core.pojo.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zsxfa
 */
@Data
@ApiModel(description="用户搜索对象")
public class UserInfoQuery {

    @ApiModelProperty(value = "手机号")
    private String telephone;
    @ApiModelProperty(value = "用户姓名")
    private String userName;
    @ApiModelProperty(value = "1:管理员 2:用户")
    private Integer role;
}
