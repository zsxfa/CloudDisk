package com.zsxfa.cloud.core.config.fileConf.operation.read.product;

import com.zsxfa.cloud.core.config.fileConf.config.MinioConfig;
import com.zsxfa.cloud.core.config.fileConf.exception.ReadException;
import com.zsxfa.cloud.core.config.fileConf.operation.read.Reader;
import com.zsxfa.cloud.core.config.fileConf.operation.read.domain.ReadFile;
import com.zsxfa.cloud.core.config.fileConf.util.ReadFileUtils;
import com.zsxfa.cloud.core.config.fileConf.util.UFOPUtils;
import io.minio.MinioClient;
import io.minio.errors.MinioException;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class MinioReader extends Reader {

    private MinioConfig minioConfig;

    public MinioReader(){

    }

    public MinioReader(MinioConfig minioConfig) {
        this.minioConfig = minioConfig;
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
        InputStream inputStream = null;
        try {
            // 使用MinIO服务的URL，端口，Access key和Secret key创建一个MinioClient对象
            MinioClient minioClient = new MinioClient(minioConfig.getEndpoint(), minioConfig.getAccessKey(), minioConfig.getSecretKey());
            // 调用statObject()来判断对象是否存在。
            // 如果不存在, statObject()抛出异常,
            // 否则则代表对象存在。
            minioClient.statObject(minioConfig.getBucketName(), fileUrl);

            // 获取"myobject"的输入流。
            inputStream = minioClient.getObject(minioConfig.getBucketName(), fileUrl);

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
