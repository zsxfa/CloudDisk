package com.zsxfa.cloud.core.pojo.dto.file;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;

@Data
@ApiOperation("重命名文件DTO")
public class RenameFileDTO {
    @ApiModelProperty(value = "用户文件id", required = true)
    private Long userFileId;

    @ApiModelProperty(value = "文件名", required = true)
    private String fileName;
}
