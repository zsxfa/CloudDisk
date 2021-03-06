package com.zsxfa.cloud.core.config.fileConf.operation.write.product;

import com.zsxfa.cloud.core.config.fileConf.config.MinioConfig;
import com.zsxfa.cloud.core.config.fileConf.operation.write.Writer;
import com.zsxfa.cloud.core.config.fileConf.operation.write.domain.WriteFile;
import com.zsxfa.cloud.core.config.fileConf.util.UFOPUtils;
import io.minio.MinioClient;
import io.minio.PutObjectOptions;
import io.minio.errors.*;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class MinioWriter extends Writer {

    private MinioConfig minioConfig;

    public MinioWriter(){

    }

    public MinioWriter(MinioConfig minioConfig) {
        this.minioConfig = minioConfig;
    }

    @Override
    public void write(InputStream inputStream, WriteFile writeFile) {

        // 使用MinIO服务的URL，端口，Access key和Secret key创建一个MinioClient对象
        try {
            MinioClient minioClient = new MinioClient(minioConfig.getEndpoint(), minioConfig.getAccessKey(), minioConfig.getSecretKey());
            // 检查存储桶是否已经存在
            boolean isExist = minioClient.bucketExists(minioConfig.getBucketName());
            if(!isExist) {
                minioClient.makeBucket(minioConfig.getBucketName());
            }
            PutObjectOptions putObjectOptions = new PutObjectOptions(inputStream.available(), inputStream.available());
            // 使用putObject上传一个文件到存储桶中。
            minioClient.putObject(minioConfig.getBucketName(), UFOPUtils.getAliyunObjectNameByFileUrl(writeFile.getFileUrl()), inputStream, putObjectOptions);

        } catch (InvalidEndpointException e) {
            e.printStackTrace();
        } catch (InvalidPortException e) {
            e.printStackTrace();
        } catch (RegionConflictException e) {
            e.printStackTrace();
        } catch (InvalidBucketNameException e) {
            e.printStackTrace();
        } catch (InsufficientDataException e) {
            e.printStackTrace();
        } catch (ErrorResponseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidResponseException e) {
            e.printStackTrace();
        } catch (XmlParserException e) {
            e.printStackTrace();
        } catch (InternalException e) {
            e.printStackTrace();
        }
    }



}
