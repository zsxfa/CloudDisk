package com.zsxfa.cloud.core.pojo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author zsxfa
 */
@Data
@ApiModel(description="文件对象")
public class FileListVo implements Serializable {
    @ApiModelProperty(value = "用户类型")
    private Long fileId;

    private String timeStampName;

    private String fileUrl;

    private Long fileSize;

    private Integer storageType;

    private Integer pointCount;

    private String identifier;

    private Long userFileId;

    private Long userId;

    private String userName;

    private String fileName;

    private String filePath;
    @ApiModelProperty(value = "扩展名")
    private String extendName;

    private Integer isDir;

    private String uploadTime;

    private Integer deleteFlag;

    private String deleteTime;

    private String deleteBatchNum;
}
