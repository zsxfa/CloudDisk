package com.zsxfa.cloud.core.pojo.dto.recoveryfile;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;

@Data
@ApiOperation("回收文件DTO")
public class RestoreFileDTO {
    @ApiModelProperty(value = "删除批次号")
    private String deleteBatchNum;
    @ApiModelProperty(value = "文件路径")
    private String filePath;
}
