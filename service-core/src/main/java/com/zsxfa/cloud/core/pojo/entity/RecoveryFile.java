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
@ApiModel(value="Recoveryfile对象", description="")
public class RecoveryFile implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "recoveryFileId", type = IdType.AUTO)
    private Long recoveryFileId;

    @TableField("deleteBatchNum")
    private String deleteBatchNum;

    @TableField("deleteTime")
    private String deleteTime;

    @TableField("userFileId")
    private Long userFileId;


}
