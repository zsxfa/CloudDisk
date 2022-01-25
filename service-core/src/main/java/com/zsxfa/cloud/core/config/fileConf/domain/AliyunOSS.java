package com.zsxfa.cloud.core.config.fileConf.domain;

import lombok.Data;

@Data
public class AliyunOSS {

    private String endpoint;
    private String accessKeyId;
    private String accessKeySecret;
    private String bucketName;
    private String objectName;
}
