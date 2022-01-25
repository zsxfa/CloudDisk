package com.zsxfa.cloud.core.service;

import com.zsxfa.cloud.core.pojo.entity.File;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zsxfa
 * @since 2021-11-13
 */
public interface FileService extends IService<File> {

    //增加文件的指向个数
    void increaseFilePointCount(Long fileId);

    //查询file对象
    File getFileInfo(Long fileId);
}
