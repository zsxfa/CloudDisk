package com.zsxfa.cloud.core.config.fileConf.operation.delete.product;

import com.zsxfa.cloud.core.config.fileConf.config.MinioConfig;
import com.zsxfa.cloud.core.config.fileConf.exception.DeleteException;
import com.zsxfa.cloud.core.config.fileConf.operation.delete.Deleter;
import com.zsxfa.cloud.core.config.fileConf.operation.delete.domain.DeleteFile;
import io.minio.MinioClient;
import io.minio.errors.MinioException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;


@Slf4j
public class MinioDeleter extends Deleter {
    private MinioConfig minioConfig;

    public MinioDeleter(){

    }

    public MinioDeleter(MinioConfig minioConfig) {
        this.minioConfig = minioConfig;
    }
    @Override
    public void delete(DeleteFile deleteFile) {

        try {
            // 使用MinIO服务的URL，端口，Access key和Secret key创建一个MinioClient对象
            MinioClient minioClient = new MinioClient(minioConfig.getEndpoint(), minioConfig.getAccessKey(), minioConfig.getSecretKey());
            // 从mybucket中删除myobject。
            minioClient.removeObject(minioConfig.getBucketName(), deleteFile.getFileUrl());
            log.info("successfully removed mybucket/myobject");
        } catch (MinioException e) {
            log.error("Error: " + e);
            throw new DeleteException(e);
        } catch (IOException e) {
            e.printStackTrace();
            throw new DeleteException(e);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new DeleteException(e);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
            throw new DeleteException(e);
        }


    }
}
