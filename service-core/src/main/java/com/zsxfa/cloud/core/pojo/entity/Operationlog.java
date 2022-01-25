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
@ApiModel(value="Operationlog对象", description="")
public class Operationlog implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "operationLogId", type = IdType.AUTO)
    private Long operationlogid;

    private String detail;

    @TableField("logLevel")
    private String loglevel;

    private String operation;

    @TableField("operationObj")
    private String operationobj;

    private String result;

    private String source;

    private String terminal;

    private String time;

    @TableField("userId")
    private Long userid;


}
