package com.zsxfa.cloud.core.config.fileConf.operation.write.product;

import com.aliyun.oss.OSS;
import com.zsxfa.cloud.core.config.fileConf.config.AliyunConfig;
import com.zsxfa.cloud.core.config.fileConf.operation.write.Writer;
import com.zsxfa.cloud.core.config.fileConf.operation.write.domain.WriteFile;
import com.zsxfa.cloud.core.config.fileConf.util.AliyunUtils;
import com.zsxfa.cloud.core.config.fileConf.util.UFOPUtils;

import java.io.InputStream;

public class AliyunOSSWriter extends Writer {

    private AliyunConfig aliyunConfig;

    public AliyunOSSWriter(){

    }

    public AliyunOSSWriter(AliyunConfig aliyunConfig) {
        this.aliyunConfig = aliyunConfig;
    }

    @Override
    public void write(InputStream inputStream, WriteFile writeFile) {
        OSS ossClient = AliyunUtils.getOSSClient(aliyunConfig);

        ossClient.putObject(aliyunConfig.getOss().getBucketName(), UFOPUtils.getAliyunObjectNameByFileUrl(writeFile.getFileUrl()), inputStream);
        ossClient.shutdown();
    }



}
