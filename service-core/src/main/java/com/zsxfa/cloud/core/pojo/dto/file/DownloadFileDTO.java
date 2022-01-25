package com.zsxfa.cloud.core.pojo.dto.file;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@ApiOperation("下载文件DTO")
public class DownloadFileDTO {
    private Long userFileId;
    private String token;
    @ApiModelProperty(value = "批次号")
    private String shareBatchNum;
    @ApiModelProperty(value = "提取码")
    private String extractionCode;
}
