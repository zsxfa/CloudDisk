package com.zsxfa.cloud.core.service;

import com.zsxfa.cloud.core.pojo.entity.Storage;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zsxfa
 */
public interface StorageService extends IService<Storage> {
    //通过id返回存储大小
    Long getTotalStorageSize(Long userId);
    //检查存储
    boolean checkStorage(Long userId, long fileSize);
}
