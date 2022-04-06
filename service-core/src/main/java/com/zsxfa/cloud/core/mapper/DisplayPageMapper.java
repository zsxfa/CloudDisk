package com.zsxfa.cloud.core.mapper;

import com.zsxfa.cloud.core.pojo.dto.view.FileSearch;

import java.util.List;

/**
 * @author zsxfa
 */
public interface DisplayPageMapper {
    //查询所有数量
    Long searchToatl(Long userId);

    //图标展示页面数量统计
    FileSearch adminDisplaySelectCountNotInExtendName(List<String> fileNameList, Long userId);
    FileSearch adminDisplaySelectCountByExtendName(List<String> fileNameList, Long userId);
}
