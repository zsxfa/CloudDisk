package com.zsxfa.cloud.core.config.fileConf.operation.delete.product;

import com.qiniu.common.QiniuException;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.util.Auth;
import com.zsxfa.cloud.core.config.fileConf.config.QiniuyunConfig;
import com.zsxfa.cloud.core.config.fileConf.operation.delete.Deleter;
import com.zsxfa.cloud.core.config.fileConf.operation.delete.domain.DeleteFile;
import com.zsxfa.cloud.core.config.fileConf.util.QiniuyunUtils;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class QiniuyunKodoDeleter extends Deleter {
    private QiniuyunConfig qiniuyunConfig;

    public QiniuyunKodoDeleter(){

    }

    public QiniuyunKodoDeleter(QiniuyunConfig qiniuyunConfig) {
        this.qiniuyunConfig = qiniuyunConfig;
    }
    @Override
    public void delete(DeleteFile deleteFile) {
        Configuration cfg = QiniuyunUtils.getCfg(qiniuyunConfig);
        Auth auth = Auth.create(qiniuyunConfig.getKodo().getAccessKey(), qiniuyunConfig.getKodo().getSecretKey());
        BucketManager bucketManager = new BucketManager(auth, cfg);
        try {
            bucketManager.delete(qiniuyunConfig.getKodo().getBucketName(), deleteFile.getFileUrl());
        } catch (QiniuException ex) {
            //如果遇到异常，说明删除失败
            System.err.println(ex.code());
            System.err.println(ex.response.toString());
        }


    }
}
