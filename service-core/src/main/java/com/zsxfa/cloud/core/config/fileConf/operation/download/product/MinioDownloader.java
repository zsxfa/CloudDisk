package com.zsxfa.cloud.core.config.fileConf.operation.download.product;

import com.zsxfa.cloud.core.config.fileConf.config.MinioConfig;
import com.zsxfa.cloud.core.config.fileConf.operation.download.Downloader;
import com.zsxfa.cloud.core.config.fileConf.operation.download.domain.DownloadFile;
import com.zsxfa.cloud.core.config.fileConf.util.IOUtils;
import io.minio.MinioClient;
import io.minio.errors.MinioException;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class MinioDownloader extends Downloader {

    private MinioConfig minioConfig;

    public MinioDownloader(){

    }

    public MinioDownloader(MinioConfig minioConfig) {
        this.minioConfig = minioConfig;
    }
    @Override
    public void download(HttpServletResponse httpServletResponse, DownloadFile downloadFile) {


        try {
            // 使用MinIO服务的URL，端口，Access key和Secret key创建一个MinioClient对象
            MinioClient minioClient = new MinioClient(minioConfig.getEndpoint(), minioConfig.getAccessKey(), minioConfig.getSecretKey());
            // 调用statObject()来判断对象是否存在。
            // 如果不存在, statObject()抛出异常,
            // 否则则代表对象存在。
            minioClient.statObject(minioConfig.getBucketName(), downloadFile.getFileUrl());

            // 获取"myobject"的输入流。
            InputStream inputStream = minioClient.getObject(minioConfig.getBucketName(), downloadFile.getFileUrl());
            IOUtils.writeInputStreamToResponse(inputStream, httpServletResponse);

        } catch (MinioException e) {
            System.out.println("Error occurred: " + e);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }

    }

    @Override
    public InputStream getInputStream(DownloadFile downloadFile) {
        InputStream inputStream = null;
        try {
            // 使用MinIO服务的URL，端口，Access key和Secret key创建一个MinioClient对象
            MinioClient minioClient = new MinioClient(minioConfig.getEndpoint(), minioConfig.getAccessKey(), minioConfig.getSecretKey());
            // 调用statObject()来判断对象是否存在。
            // 如果不存在, statObject()抛出异常,
            // 否则则代表对象存在。
            minioClient.statObject(minioConfig.getBucketName(), downloadFile.getFileUrl());

            // 获取"myobject"的输入流。
            inputStream = minioClient.getObject(minioConfig.getBucketName(), downloadFile.getFileUrl());

        } catch (MinioException e) {
            System.out.println("Error occurred: " + e);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }


        return inputStream;
    }

}
