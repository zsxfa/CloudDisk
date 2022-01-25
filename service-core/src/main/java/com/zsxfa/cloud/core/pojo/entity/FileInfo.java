package com.zsxfa.cloud.core.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author zsxfa
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="File对象", description="文件对象")
public class FileInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "编号")
    @TableId(value = "fid", type = IdType.AUTO)
    private Integer fid;
    @ApiModelProperty(value = "文件名称")
    private String fname;
    @ApiModelProperty(value = "文件大小")
    private Integer fsize;
    @ApiModelProperty(value = "文件类型")
    private Integer ftype;
    @ApiModelProperty(value = "上传时间")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime ftime;
    @ApiModelProperty(value = "收藏状态")
    private Integer fcollect;
    @ApiModelProperty(value = "删除状态")
    private Integer fdelete;
    @ApiModelProperty(value = "文件删除时间")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime fdtime;
    @ApiModelProperty(value = "存储位置")
    private String fpath;
    @ApiModelProperty(value = "文件后缀")
    private String fsuffix;
    @ApiModelProperty(value = "所属用户")
    private Integer fowner;
    @ApiModelProperty(value = "所属用户名")
    private String fownername;
    @ApiModelProperty(value = "文件夹中的文件号")
    private Integer ffolder;
    @ApiModelProperty(value = "分享")
    private Integer fshare;
}
