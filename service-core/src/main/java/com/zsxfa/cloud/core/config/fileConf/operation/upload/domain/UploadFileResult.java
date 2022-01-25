package com.zsxfa.cloud.core.config.fileConf.operation.upload.domain;

import com.zsxfa.cloud.core.config.fileConf.constant.StorageTypeEnum;
import com.zsxfa.cloud.core.config.fileConf.constant.UploadFileStatusEnum;
import lombok.Data;

@Data
public class UploadFileResult {
    private String fileName;
    private String extendName;
    private long fileSize;
    private String fileUrl;
    private StorageTypeEnum storageType;
    private UploadFileStatusEnum status;

}
