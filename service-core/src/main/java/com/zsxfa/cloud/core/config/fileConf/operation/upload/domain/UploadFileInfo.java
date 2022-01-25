package com.zsxfa.cloud.core.config.fileConf.operation.upload.domain;

import lombok.Data;

@Data
    public class UploadFileInfo {
        private String bucketName;
        private String key;
        private String uploadId;
    }