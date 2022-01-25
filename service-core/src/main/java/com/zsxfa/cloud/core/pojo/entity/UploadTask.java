package com.zsxfa.cloud.core.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableName;
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
 */
@Data
@TableName("uploadtask")
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="Uploadtask对象", description="")
public class UploadTask implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "uploadTaskId", type = IdType.AUTO)
    private Long uploadTaskId;

    @TableField("extendName")
    private String extendName;

    @TableField("fileName")
    private String fileName;

    @TableField("filePath")
    private String filePath;

    private String identifier;

    @TableField("uploadStatus")
    private Integer uploadStatus;

    @TableField("uploadTime")
    private String uploadTime;

    @TableField("userId")
    private Long userId;


}
