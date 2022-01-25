package com.zsxfa.cloud.core.pojo.dto.file;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;

@Data
@ApiOperation("删除回收文件DTO")
public class DeleteRecoveryFileDTO {

    @ApiModelProperty(value = "回收文件id")
    private Long recoveryFileId;

}
