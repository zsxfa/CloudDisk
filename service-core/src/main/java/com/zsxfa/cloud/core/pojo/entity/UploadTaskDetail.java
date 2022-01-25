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
 * @since 2021-11-13
 */
@Data
@TableName("uploadtaskdetail")
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="Uploadtaskdetail对象", description="")
public class UploadTaskDetail implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "uploadTaskDetailId", type = IdType.AUTO)
    private Long uploadTaskDetailId;

    @TableField("chunkNumber")
    private Integer chunkNumber;

    @TableField("chunkSize")
    private Integer chunkSize;

    @TableField("filePath")
    private String filePath;

    @TableField("filename")
    private String fileName;

    private String identifier;

    @TableField("relativePath")
    private String relativePath;

    @TableField("totalChunks")
    private Integer totalChunks;

    @TableField("totalSize")
    private Integer totalSize;


}
