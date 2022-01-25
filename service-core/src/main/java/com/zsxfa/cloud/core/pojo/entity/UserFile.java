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
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="Userfile对象", description="")
public class UserFile implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "userFileId", type = IdType.AUTO)
    private Long userFileId;

    @TableField("deleteBatchNum")
    private String deleteBatchNum;

    @TableField("deleteFlag")
    private Integer deleteFlag;

    @TableField("deleteTime")
    private String deleteTime;

    @TableField("extendName")
    private String extendName;

    @TableField("fileId")
    private Long fileId;

    @TableField("fileName")
    private String fileName;

    @TableField("filePath")
    private String filePath;

    @TableField("isDir")
    private Integer isDir;

    @TableField("uploadTime")
    private String uploadTime;

    @TableField("userId")
    private Long userId;


}
