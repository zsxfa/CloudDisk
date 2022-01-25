package com.zsxfa.cloud.core.config.fileConf.config;

import com.zsxfa.cloud.core.config.fileConf.domain.AliyunOSS;
import lombok.Data;

@Data
public class  AliyunConfig {
    private AliyunOSS oss = new AliyunOSS();


}
