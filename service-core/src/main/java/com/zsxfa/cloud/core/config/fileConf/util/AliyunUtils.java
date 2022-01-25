package com.zsxfa.cloud.core.config.fileConf.util;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.zsxfa.cloud.core.config.fileConf.config.AliyunConfig;

public class AliyunUtils {

    public static OSS getOSSClient(AliyunConfig aliyunConfig) {
        OSS ossClient = new OSSClientBuilder().build(aliyunConfig.getOss().getEndpoint(),
                aliyunConfig.getOss().getAccessKeyId(),
                aliyunConfig.getOss().getAccessKeySecret());;
        return ossClient;
    }

}
