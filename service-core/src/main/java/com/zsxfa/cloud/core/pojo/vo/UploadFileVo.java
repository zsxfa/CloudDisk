package com.zsxfa.cloud.core.pojo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @author zsxfa
 */
@Data
@ApiModel(description="文件上传Vo")
public class UploadFileVo {

    @ApiModelProperty(value = "时间戳", example = "123123123123")
    private String timeStampName;
    @ApiModelProperty(value = "跳过上传", example = "true")
    private boolean skipUpload;
    @ApiModelProperty(value = "是否需要合并分片", example = "true")
    private boolean needMerge;
    @ApiModelProperty(value = "已经上传的分片", example = "[1,2,3]")
    private List<Integer> uploaded;

}
