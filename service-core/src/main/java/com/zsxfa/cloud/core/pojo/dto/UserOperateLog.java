package com.zsxfa.cloud.core.pojo.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;

/**
 * @author zsxfa
 */
@Data
@ApiOperation("用户操作日志DTO")
public class UserOperateLog {

    private String detail;

    private String operation;

    private String result;

    private String source;

    private String time;

    @TableField("userId")
    private Long userid;

    private String username;

}
