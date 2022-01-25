package com.zsxfa.cloud.core.pojo.dto.file;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@ApiOperation("预览文件DTO")
public class PreviewDTO {
    private Long userFileId;
    private String token;
    @ApiModelProperty(value = "批次号")
    private String shareBatchNum;
    @ApiModelProperty(value = "提取码")
    private String extractionCode;
    @ApiModelProperty(value = "是否缩略图")
    private String isMin;
}
