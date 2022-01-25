package com.zsxfa.cloud.core.config.fileConf.config;

import com.zsxfa.cloud.core.config.fileConf.domain.QiniuyunKodo;
import lombok.Data;

@Data
public class QiniuyunConfig {
    private QiniuyunKodo kodo = new QiniuyunKodo();
}
