package com.zsxfa.cloud.core.pojo.dto.file;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;

@Data
@ApiOperation("删除文件DTO")
public class DeleteFileDTO {

    @ApiModelProperty(value = "用户文件id", required = true)
    private Long userFileId;


}
