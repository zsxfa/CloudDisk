package com.zsxfa.cloud.core.config.fileConf.autoconfiguration;


import com.zsxfa.cloud.core.config.fileConf.config.AliyunConfig;
import com.zsxfa.cloud.core.config.fileConf.config.MinioConfig;
import com.zsxfa.cloud.core.config.fileConf.config.QiniuyunConfig;
import com.zsxfa.cloud.core.config.fileConf.domain.ThumbImage;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@ConfigurationProperties(prefix = "ufop")
public class UFOPProperties {

    private String storageType;
    private String localStoragePath;
    private AliyunConfig aliyun = new AliyunConfig();
    private ThumbImage thumbImage = new ThumbImage();
    private MinioConfig minio = new MinioConfig();
    private QiniuyunConfig qiniuyun = new QiniuyunConfig();
}
