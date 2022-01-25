package com.zsxfa.cloud.core.pojo.dto.file;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;


@Data
@ApiOperation("上传文件DTO")
public class UploadFileDTO {

    @ApiModelProperty(value = "文件路径")
    private String filePath;

    @ApiModelProperty(value = "文件名")
    private String filename;

    @ApiModelProperty(value = "切片数量")
    private int chunkNumber;

    @ApiModelProperty(value = "切片大小")
    private long chunkSize;
    @ApiModelProperty(value = "相对路径")
    private String relativePath;

    @ApiModelProperty(value = "所有切片")
    private int totalChunks;
    @ApiModelProperty(value = "总大小")
    private long totalSize;
    @ApiModelProperty(value = "当前切片大小")
    private long currentChunkSize;
    @ApiModelProperty(value = "md5码")
    private String identifier;

}
