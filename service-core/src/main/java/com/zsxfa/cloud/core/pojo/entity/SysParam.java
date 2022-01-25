package com.zsxfa.cloud.core.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
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
@ApiModel(value="Sysparam对象", description="")
public class SysParam implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "sysParamId", type = IdType.AUTO)
    private Long sysParamId;

    @TableField("sysParamDesc")
    private String sysParamDesc;

    @TableField("sysParamKey")
    private String sysParamKey;

    @TableField("sysParamValue")
    private String sysParamValue;


}
