package com.zsxfa.security.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 用户实体类
 * </p>
 *
 * @author zsxfa
 */
@Data
@ApiModel(description = "用户实体类")
//@JsonIgnoreProperties(ignoreUnknown = true)
public class User implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty("password")
	private String password;

	@ApiModelProperty("username")
	private String username;

//	@ApiModelProperty(value = "微信openid")
//	private String userName;
//
//	@ApiModelProperty(value = "密码")
//	private String password;
//
//	@ApiModelProperty(value = "昵称")
//	private String nickName;
//
//	@ApiModelProperty(value = "用户头像")
//	private String salt;
//
//	@ApiModelProperty(value = "用户签名")
//	private String token;
}



