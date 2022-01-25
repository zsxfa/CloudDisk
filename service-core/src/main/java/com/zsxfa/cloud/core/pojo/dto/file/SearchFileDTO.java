package com.zsxfa.cloud.core.pojo.dto.file;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@ApiOperation("查询文件DTO")
public class SearchFileDTO {
    @ApiModelProperty(value = "文件名", required=true)
    private String fileName;
    @ApiModelProperty(value = "当前页", required=true)
    private long currentPage;
    @ApiModelProperty(value = "每页数量", required=true)
    private long pageCount;
    @ApiModelProperty(value = "排序字段(可排序字段：fileName, fileSize, extendName, uploadTime)", required=false)
    private String order;
    @ApiModelProperty(value = "方向(升序：asc, 降序：desc)", required=false)
    private String direction;
}
