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
@ApiModel(value="Storage对象", description="")
public class Storage implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "storageId", type = IdType.AUTO)
    private Long storageId;

    @TableField("storageSize")
    private Long storageSize;

    @TableField("totalStorageSize")
    private Long totalStorageSize;

    @TableField("userId")
    private Long userId;


}
