package com.zsxfa.cloud.core.config.fileConf.config;

import lombok.Data;

@Data
public class MinioConfig {
    private String endpoint;
    private String accessKey;
    private String secretKey;
    private String bucketName;
}
