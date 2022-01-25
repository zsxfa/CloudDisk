package com.zsxfa.cloud.core.pojo.dto.file;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;

@Data
@ApiOperation("批量移动文件DTO")
public class BatchMoveFileDTO {
    @ApiModelProperty(value = "文件集合", required = true)
    private String files;
    @ApiModelProperty(value = "目的文件路径", required = true)
    private String filePath;


}
