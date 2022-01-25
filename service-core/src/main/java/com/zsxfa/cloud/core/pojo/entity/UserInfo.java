package com.zsxfa.cloud.core.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 用户基本信息
 * </p>
 *
 * @author zsxfa
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="UserInfo对象", description="用户基本信息")
public class UserInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "编号")
      @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "手机号")
    private String mobile;

    @ApiModelProperty(value = "用户密码")
    private String password;

    @ApiModelProperty(value = "用户姓名")
    private String name;

    @ApiModelProperty(value = "头像")
    private String headImg;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updateTime;

    @ApiModelProperty(value = "用户角色")
    private Integer role;


}
