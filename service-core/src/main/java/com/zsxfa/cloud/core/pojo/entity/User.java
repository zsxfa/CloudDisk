package com.zsxfa.cloud.core.pojo.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import java.util.Date;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author zsxfa
 * @since 2021-11-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="User对象", description="")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "用户ID")
    @TableId(value = "userId", type = IdType.AUTO)
    private Long userId;

    @ApiModelProperty(value = "邮箱")
    private String email;

    @ApiModelProperty(value = "图片地址")
    @TableField("imageUrl")
    private String imageurl;

    @ApiModelProperty(value = "密码")
    private String password;

    @ApiModelProperty(value = "salt")
    private String salt;

    @ApiModelProperty(value = "电话")
    private String telephone;

    @ApiModelProperty(value = "用户名")
    private String username;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date gmtCreate;

    @ApiModelProperty(value = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date gmtModified;


}
