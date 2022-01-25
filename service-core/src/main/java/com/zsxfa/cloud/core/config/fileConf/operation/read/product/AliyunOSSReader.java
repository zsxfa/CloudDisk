package com.zsxfa.cloud.core.config.fileConf.operation.read.product;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.OSSObject;
import com.zsxfa.cloud.core.config.fileConf.config.AliyunConfig;
import com.zsxfa.cloud.core.config.fileConf.exception.ReadException;
import com.zsxfa.cloud.core.config.fileConf.operation.read.Reader;
import com.zsxfa.cloud.core.config.fileConf.operation.read.domain.ReadFile;
import com.zsxfa.cloud.core.config.fileConf.util.AliyunUtils;
import com.zsxfa.cloud.core.config.fileConf.util.ReadFileUtils;
import com.zsxfa.cloud.core.config.fileConf.util.UFOPUtils;

import java.io.IOException;
import java.io.InputStream;

public class AliyunOSSReader extends Reader {

    private AliyunConfig aliyunConfig;

    public AliyunOSSReader(){

    }

    public AliyunOSSReader(AliyunConfig aliyunConfig) {
        this.aliyunConfig = aliyunConfig;
    }

    @Override
    public String read(ReadFile readFile) {
        String fileUrl = readFile.getFileUrl();
        String fileType = UFOPUtils.getFileExtendName(fileUrl);
        try {
            return ReadFileUtils.getContentByInputStream(fileType, getInputStream(readFile.getFileUrl()));
        } catch (IOException e) {
            throw new ReadException("读取文件失败", e);
        }
    }

    public InputStream getInputStream(String fileUrl) {
        OSS ossClient = AliyunUtils.getOSSClient(aliyunConfig);
        OSSObject ossObject = ossClient.getObject(aliyunConfig.getOss().getBucketName(),
                UFOPUtils.getAliyunObjectNameByFileUrl(fileUrl));
        InputStream inputStream = ossObject.getObjectContent();
        return inputStream;
    }

}
