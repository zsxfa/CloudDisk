package com.zsxfa.cloud.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zsxfa.cloud.core.mapper.SysParamMapper;
import com.zsxfa.cloud.core.mapper.UserFileMapper;
import com.zsxfa.cloud.core.pojo.entity.Storage;
import com.zsxfa.cloud.core.mapper.StorageMapper;
import com.zsxfa.cloud.core.pojo.entity.SysParam;
import com.zsxfa.cloud.core.service.StorageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
@Service
@Transactional(rollbackFor=Exception.class)
public class StorageServiceImpl extends ServiceImpl<StorageMapper, Storage> implements StorageService {

    @Resource
    StorageMapper storageMapper;
    @Resource
    SysParamMapper sysParamMapper;
    @Resource
    UserFileMapper userFileMapper;

    @Override
    public Long getTotalStorageSize(Long userId) {
        LambdaQueryWrapper<Storage> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Storage::getUserId, userId);

        Storage storage = storageMapper.selectOne(lambdaQueryWrapper);
        Long totalStorageSize = null;
        if (storage == null || storage.getTotalStorageSize() == null) {
            LambdaQueryWrapper<SysParam> lambdaQueryWrapper1 = new LambdaQueryWrapper<>();
            lambdaQueryWrapper1.eq(SysParam::getSysParamKey, "totalStorageSize");
            SysParam sysParam = sysParamMapper.selectOne(lambdaQueryWrapper1);
            totalStorageSize = Long.parseLong(sysParam.getSysParamValue());
            storage = new Storage();
            storage.setUserId(userId);
            storage.setTotalStorageSize(totalStorageSize);
            storageMapper.insert(storage);
        } else  {
            totalStorageSize = storage.getTotalStorageSize();
        }

        if (totalStorageSize != null) {
            totalStorageSize = totalStorageSize * 1024 * 1024;
        }
        return totalStorageSize;
    }

    @Override
    public boolean checkStorage(Long userId, long fileSize) {
        LambdaQueryWrapper<Storage> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Storage::getUserId, userId);

        Storage storageBean = storageMapper.selectOne(lambdaQueryWrapper);
        Long totalStorageSize = null;
        if (storageBean == null || storageBean.getTotalStorageSize() == null) {
            LambdaQueryWrapper<SysParam> lambdaQueryWrapper1 = new LambdaQueryWrapper<>();
            lambdaQueryWrapper1.eq(SysParam::getSysParamKey, "totalStorageSize");
            SysParam sysParam = sysParamMapper.selectOne(lambdaQueryWrapper1);
            totalStorageSize = Long.parseLong(sysParam.getSysParamValue());
            storageBean = new Storage();
            storageBean.setUserId(userId);
            storageBean.setTotalStorageSize(totalStorageSize);
            storageMapper.insert(storageBean);
        } else  {
            totalStorageSize = storageBean.getTotalStorageSize();
        }

        if (totalStorageSize != null) {
            totalStorageSize = totalStorageSize * 1024 * 1024;
        }

        Long storageSize = userFileMapper.selectStorageSizeByUserId(userId);
        if (storageSize == null ){
            storageSize = 0L;
        }
        if (storageSize + fileSize > totalStorageSize) {
            return false;
        }
        return true;

    }
}
