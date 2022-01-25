package com.zsxfa.cloud.core.service.impl;

import com.zsxfa.cloud.core.pojo.entity.File;
import com.zsxfa.cloud.core.mapper.FileMapper;
import com.zsxfa.cloud.core.service.FileService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zsxfa
 * @since 2021-11-13
 */
@Slf4j
@Service
@Transactional(rollbackFor=Exception.class)
public class FileServiceImpl extends ServiceImpl<FileMapper, File> implements FileService {
    @Resource
    FileMapper fileMapper;


    @Override
    public void increaseFilePointCount(Long fileId) {
        File fileBean = fileMapper.selectById(fileId);
        if (fileBean == null) {
            log.error("文件不存在，fileId : {}", fileId );
            return;
        }
        fileBean.setPointCount(fileBean.getPointCount()+1);
        fileMapper.updateById(fileBean);
    }

    @Override
    public File getFileInfo(Long fileId) {
        return fileMapper.selectById(fileId);
    }
}
