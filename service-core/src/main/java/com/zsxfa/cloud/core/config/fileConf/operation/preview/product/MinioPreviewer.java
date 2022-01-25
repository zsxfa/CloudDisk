package com.zsxfa.cloud.core.config.fileConf.operation.preview.product;

import com.zsxfa.cloud.core.config.fileConf.config.MinioConfig;
import com.zsxfa.cloud.core.config.fileConf.domain.ThumbImage;
import com.zsxfa.cloud.core.config.fileConf.operation.preview.Previewer;
import io.minio.MinioClient;
import io.minio.errors.MinioException;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Getter
@Setter
@Slf4j
public class MinioPreviewer extends Previewer {
    private MinioConfig minioConfig;

    public MinioPreviewer(){

    }

    public MinioPreviewer(MinioConfig minioConfig, ThumbImage thumbImage) {
        setMinioConfig(minioConfig);
        setThumbImage(thumbImage);
    }

    @Override
    public InputStream getInputStream(String fileUrl) {
        InputStream inputStream = null;
        try {
            // 使用MinIO服务的URL，端口，Access key和Secret key创建一个MinioClient对象
            MinioClient minioClient = new MinioClient(getMinioConfig().getEndpoint(), getMinioConfig().getAccessKey(), getMinioConfig().getSecretKey());
            minioClient.statObject(minioConfig.getBucketName(), fileUrl);
            inputStream = minioClient.getObject(minioConfig.getBucketName(), fileUrl);

        } catch (MinioException e) {
            System.out.println("Error occurred: " + e);
        } catch (IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            log.error(e.getMessage());
        }


        return inputStream;
    }


}
