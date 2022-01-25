package com.zsxfa.cloud.core.config.fileConf.operation.delete.product;

import com.aliyun.oss.OSS;
import com.zsxfa.cloud.core.config.fileConf.config.AliyunConfig;
import com.zsxfa.cloud.core.config.fileConf.operation.delete.Deleter;
import com.zsxfa.cloud.core.config.fileConf.operation.delete.domain.DeleteFile;
import com.zsxfa.cloud.core.config.fileConf.util.AliyunUtils;
import com.zsxfa.cloud.core.config.fileConf.util.UFOPUtils;


public class AliyunOSSDeleter extends Deleter {
    private AliyunConfig aliyunConfig;

    public AliyunOSSDeleter(){

    }

    public AliyunOSSDeleter(AliyunConfig aliyunConfig) {
        this.aliyunConfig = aliyunConfig;
    }
    @Override
    public void delete(DeleteFile deleteFile) {
        OSS ossClient = AliyunUtils.getOSSClient(aliyunConfig);
        try {
            ossClient.deleteObject(aliyunConfig.getOss().getBucketName(), UFOPUtils.getAliyunObjectNameByFileUrl(deleteFile.getFileUrl()));
        } finally {
            ossClient.shutdown();
        }

    }
}
