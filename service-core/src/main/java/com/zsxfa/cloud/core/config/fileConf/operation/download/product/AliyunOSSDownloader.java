package com.zsxfa.cloud.core.config.fileConf.operation.download.product;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.OSSObject;
import com.zsxfa.cloud.core.config.fileConf.config.AliyunConfig;
import com.zsxfa.cloud.core.config.fileConf.operation.download.Downloader;
import com.zsxfa.cloud.core.config.fileConf.operation.download.domain.DownloadFile;
import com.zsxfa.cloud.core.config.fileConf.util.AliyunUtils;
import com.zsxfa.cloud.core.config.fileConf.util.IOUtils;
import com.zsxfa.cloud.core.config.fileConf.util.UFOPUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;

public class AliyunOSSDownloader extends Downloader {

    private AliyunConfig aliyunConfig;

    public AliyunOSSDownloader(){

    }

    public AliyunOSSDownloader(AliyunConfig aliyunConfig) {
        this.aliyunConfig = aliyunConfig;
    }
    @Override
    public void download(HttpServletResponse httpServletResponse, DownloadFile downloadFile) {



        OSS ossClient = AliyunUtils.getOSSClient(aliyunConfig);
        OSSObject ossObject = ossClient.getObject(aliyunConfig.getOss().getBucketName(),
                UFOPUtils.getAliyunObjectNameByFileUrl(downloadFile.getFileUrl()));
        InputStream inputStream = ossObject.getObjectContent();

        IOUtils.writeInputStreamToResponse(inputStream, httpServletResponse);
        ossClient.shutdown();
    }



    @Override
    public InputStream getInputStream(DownloadFile downloadFile) {
        OSS ossClient = AliyunUtils.getOSSClient(aliyunConfig);
        OSSObject ossObject = ossClient.getObject(aliyunConfig.getOss().getBucketName(),
                UFOPUtils.getAliyunObjectNameByFileUrl(downloadFile.getFileUrl()));
        InputStream inputStream = ossObject.getObjectContent();
        return inputStream;
    }

}
