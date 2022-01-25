package com.zsxfa.cloud.core.pojo.dto.file;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;

import java.util.regex.Pattern;

@Data
@ApiOperation("批量删除文件DTO")
public class BatchDeleteFileDTO {
    @ApiModelProperty(value = "文件集合", required = true)
    private String files;

}
