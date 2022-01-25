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
@ApiModel(value="File对象", description="")
public class File implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "fileId", type = IdType.AUTO)
    private Long fileId;

    @TableField("fileSize")
    private Long fileSize;

    @TableField("fileUrl")
    private String fileUrl;

    private String identifier;

    @TableField("pointCount")
    private Integer pointCount;

    @TableField("storageType")
    private Integer storageType;


}
