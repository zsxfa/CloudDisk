package com.zsxfa.cloud.core.config.fileConf.operation.copy.product;

import com.aliyun.oss.OSS;
import com.zsxfa.cloud.base.util.UFOPUtils;
import com.zsxfa.cloud.core.config.fileConf.config.AliyunConfig;
import com.zsxfa.cloud.core.config.fileConf.operation.copy.Copier;
import com.zsxfa.cloud.core.config.fileConf.operation.copy.domain.CopyFile;
import com.zsxfa.cloud.core.config.fileConf.util.AliyunUtils;

import java.io.InputStream;
import java.util.UUID;

public class AliyunOSSCopier extends Copier {

    private AliyunConfig aliyunConfig;

    public AliyunOSSCopier(){

    }

    public AliyunOSSCopier(AliyunConfig aliyunConfig) {
        this.aliyunConfig = aliyunConfig;
    }
    @Override
    public String copy(InputStream inputStream, CopyFile copyFile) {
        String uuid = UUID.randomUUID().toString();
        String fileUrl = UFOPUtils.getUploadFileUrl(uuid, copyFile.getExtendName());
        OSS ossClient = AliyunUtils.getOSSClient(aliyunConfig);
        try {
            ossClient.putObject(aliyunConfig.getOss().getBucketName(), fileUrl, inputStream);
        } finally {
            ossClient.shutdown();
        }
        return fileUrl;
    }

}
