package com.zsxfa.cloud.core.pojo.dto.file;

import com.zsxfa.cloud.base.util.RegexConstant;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@ApiOperation("创建文件夹DTO")
public class CreateFileDTO {
    @ApiModelProperty(value = "文件名", required=true)
    @NotBlank(message = "文件名不能为空")
    @Pattern(regexp = RegexConstant.FILE_NAME_REGEX, message = "文件名不合法！")
    private String fileName;
    @ApiModelProperty(value = "文件路径", required=true)
    private String filePath;

}
