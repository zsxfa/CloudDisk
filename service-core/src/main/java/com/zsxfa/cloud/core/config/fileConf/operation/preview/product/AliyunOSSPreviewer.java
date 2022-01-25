package com.zsxfa.cloud.core.config.fileConf.operation.preview.product;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.OSSObject;
import com.zsxfa.cloud.core.config.fileConf.config.AliyunConfig;
import com.zsxfa.cloud.core.config.fileConf.domain.ThumbImage;
import com.zsxfa.cloud.core.config.fileConf.operation.preview.Previewer;
import com.zsxfa.cloud.core.config.fileConf.util.AliyunUtils;
import com.zsxfa.cloud.core.config.fileConf.util.UFOPUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;

@Data
@Slf4j
public class AliyunOSSPreviewer extends Previewer {


    private AliyunConfig aliyunConfig;

    public AliyunOSSPreviewer(){

    }

    public AliyunOSSPreviewer(AliyunConfig aliyunConfig, ThumbImage thumbImage) {
        this.aliyunConfig = aliyunConfig;
        setThumbImage(thumbImage);
    }


    @Override
    public InputStream getInputStream(String fileUrl) {
        OSS ossClient = AliyunUtils.getOSSClient(aliyunConfig);
        OSSObject ossObject = ossClient.getObject(aliyunConfig.getOss().getBucketName(),
                UFOPUtils.getAliyunObjectNameByFileUrl(fileUrl));
        InputStream inputStream = ossObject.getObjectContent();
        return inputStream;
    }


}
