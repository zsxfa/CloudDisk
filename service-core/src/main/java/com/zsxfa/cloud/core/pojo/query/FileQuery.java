package com.zsxfa.cloud.core.pojo.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zsxfa
 */
@Data
@ApiModel(description="文件搜索对象")
public class FileQuery {

    @ApiModelProperty(value = "文件路径")
    private String filePath;
    @ApiModelProperty(value = "文件类型")
    private Integer fileType;
    @ApiModelProperty(value = "当前页")
    private Long currentPage;
    @ApiModelProperty(value = "页面数量")
    private Long pageCount;
}
