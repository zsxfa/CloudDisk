package com.zsxfa.cloud.core.service;

import com.zsxfa.cloud.core.pojo.dto.view.DisplayPage;
import com.zsxfa.cloud.core.pojo.dto.view.FileSearch;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zsxfa
 */
public interface DisplayPageService {

    //按文件种类获取数量
    Long search(Long userId);

    //图标展示页面数量统计
    FileSearch adminDisplaySelectCountNotInExtendNames(List<String> fileExtendsByType);
    FileSearch adminDisplaySelectCountByExtendName(List<String> fileExtendsByType);
}
