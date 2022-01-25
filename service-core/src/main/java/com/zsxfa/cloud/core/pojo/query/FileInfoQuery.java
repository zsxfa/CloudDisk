package com.zsxfa.cloud.core.pojo.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zsxfa
 */
@Data
@ApiModel(description="文件搜索对象")
public class FileInfoQuery {

    @ApiModelProperty(value = "文件名称")
    private String fileName;
    @ApiModelProperty(value = "1:图片 2:文档 3:视频 4:音乐 5:其他")
    private Integer fileType;
}
