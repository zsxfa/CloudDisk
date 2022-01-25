package com.zsxfa.cloud.core.pojo.dto.recoveryfile;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@ApiOperation("批量删除回收文件DTO")
public class BatchDeleteRecoveryFileDTO {
    @ApiModelProperty(value = "恢复文件集合")
    private String recoveryFileIds;
}
