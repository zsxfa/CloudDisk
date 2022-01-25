package com.zsxfa.cloud.core.pojo.dto.view;

import lombok.Data;

/**
 * @author zsxfa
 */
//数据分析页面
@Data
public class DisplayPage {
    private Long allCount;//所有文件数量
    private Long pictureCount;//图片数量
    private Long docCount;//文档数量
    private Long musicCount;//音乐数量
    private Long videoCount;//视频数量
    private Long otherCount;//其他数量
    public String[] abc;//指某一类文件七天分别的数量
    private String[] date;//日期


}
