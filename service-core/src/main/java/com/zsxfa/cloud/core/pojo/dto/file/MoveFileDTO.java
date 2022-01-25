package com.zsxfa.cloud.core.pojo.dto.file;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;

@Data
@ApiOperation("移动文件DTO")
public class MoveFileDTO {

    @ApiModelProperty(value = "文件路径", required = true)
    private String filePath;

    @ApiModelProperty(value = "文件名", required = true)
    private String fileName;

    @ApiModelProperty(value = "旧文件路径", required = true)
    private String oldFilePath;
    @ApiModelProperty(value = "扩展名", required = true)
    private String extendName;

}
