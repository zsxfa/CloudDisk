package com.zsxfa.cloud.core.service.impl;

import com.zsxfa.cloud.core.mapper.DisplayPageMapper;
import com.zsxfa.cloud.core.pojo.dto.view.DisplayPage;
import com.zsxfa.cloud.core.pojo.dto.view.FileSearch;
import com.zsxfa.cloud.core.service.DisplayPageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author zsxfa
 */
@Slf4j
@Service
@Transactional(rollbackFor=Exception.class)
public class DisplayPageServiceImpl implements DisplayPageService {

    @Resource
    DisplayPageMapper displayPageMapper;

    @Override
    public Long search(Long userId) {
        return displayPageMapper.searchToatl(userId);
    }

    @Override
    public FileSearch adminDisplaySelectCountNotInExtendNames(List<String> fileNameList, Long userId) {
        return displayPageMapper.adminDisplaySelectCountNotInExtendName(fileNameList,userId);
    }
    @Override
    public FileSearch adminDisplaySelectCountByExtendName(List<String> fileNameList, Long userId) {
        return displayPageMapper.adminDisplaySelectCountByExtendName(fileNameList,userId);
    }
}
