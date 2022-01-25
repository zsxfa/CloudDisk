package com.zsxfa.cloud.core.config.fileConf.operation.upload.product;


import com.alibaba.fastjson.JSON;
import com.aliyun.oss.OSS;
import com.aliyun.oss.model.*;
import com.zsxfa.cloud.core.config.fileConf.config.AliyunConfig;
import com.zsxfa.cloud.core.config.fileConf.constant.StorageTypeEnum;
import com.zsxfa.cloud.core.config.fileConf.constant.UploadFileStatusEnum;
import com.zsxfa.cloud.core.config.fileConf.operation.upload.Uploader;
import com.zsxfa.cloud.core.config.fileConf.operation.upload.domain.UploadFile;
import com.zsxfa.cloud.core.config.fileConf.operation.upload.domain.UploadFileInfo;
import com.zsxfa.cloud.core.config.fileConf.operation.upload.domain.UploadFileResult;
import com.zsxfa.cloud.core.config.fileConf.operation.upload.request.QiwenMultipartFile;
import com.zsxfa.cloud.core.config.fileConf.util.AliyunUtils;
import com.zsxfa.cloud.core.config.fileConf.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Slf4j
//@Component
public class AliyunOSSUploader extends Uploader {

    @Resource
    RedisUtil redisUtil;

    private AliyunConfig aliyunConfig;

    public AliyunOSSUploader(){

    }

    public AliyunOSSUploader(AliyunConfig aliyunConfig) {
        this.aliyunConfig = aliyunConfig;
    }

    @Override
    protected void doUploadFileChunk(QiwenMultipartFile qiwenMultipartFile, UploadFile uploadFile) throws IOException {

        OSS ossClient = AliyunUtils.getOSSClient(aliyunConfig);
        try {
            UploadFileInfo uploadFileInfo = JSON.parseObject(redisUtil.getObject("QiwenUploader:Identifier:" + uploadFile.getIdentifier() + ":uploadPartRequest"), UploadFileInfo.class);
            String fileUrl = qiwenMultipartFile.getFileUrl();
            if (uploadFileInfo == null) {

                InitiateMultipartUploadRequest request = new InitiateMultipartUploadRequest(aliyunConfig.getOss().getBucketName(), fileUrl);
                InitiateMultipartUploadResult upresult = ossClient.initiateMultipartUpload(request);
                String uploadId = upresult.getUploadId();

                uploadFileInfo = new UploadFileInfo();
                uploadFileInfo.setBucketName(aliyunConfig.getOss().getBucketName());
                uploadFileInfo.setKey(fileUrl);
                uploadFileInfo.setUploadId(uploadId);

                redisUtil.set("QiwenUploader:Identifier:" + uploadFile.getIdentifier() + ":uploadPartRequest", JSON.toJSONString(uploadFileInfo));

            }

            UploadPartRequest uploadPartRequest = new UploadPartRequest();
            uploadPartRequest.setBucketName(uploadFileInfo.getBucketName());
            uploadPartRequest.setKey(uploadFileInfo.getKey());
            uploadPartRequest.setUploadId(uploadFileInfo.getUploadId());
            uploadPartRequest.setInputStream(qiwenMultipartFile.getUploadInputStream());
            uploadPartRequest.setPartSize(qiwenMultipartFile.getSize());
            uploadPartRequest.setPartNumber(uploadFile.getChunkNumber());
            log.debug(JSON.toJSONString(uploadPartRequest));

            UploadPartResult uploadPartResult = ossClient.uploadPart(uploadPartRequest);

            log.debug("上传结果：" + JSON.toJSONString(uploadPartResult));

            if (redisUtil.hasKey("QiwenUploader:Identifier:" + uploadFile.getIdentifier() + ":partETags")) {
                List<PartETag> partETags = JSON.parseArray(redisUtil.getObject("QiwenUploader:Identifier:" + uploadFile.getIdentifier() + ":partETags"), PartETag.class);
                partETags.add(uploadPartResult.getPartETag());
                redisUtil.set("QiwenUploader:Identifier:" + uploadFile.getIdentifier() + ":partETags", JSON.toJSONString(partETags));
            } else {
                List<PartETag> partETags = new ArrayList<PartETag>();
                partETags.add(uploadPartResult.getPartETag());
                redisUtil.set("QiwenUploader:Identifier:" + uploadFile.getIdentifier() + ":partETags", JSON.toJSONString(partETags));
            }
        } finally {
            ossClient.shutdown();
        }


    }

    @Override
    protected UploadFileResult organizationalResults(QiwenMultipartFile qiwenMultipartFile, UploadFile uploadFile) {
        UploadFileResult uploadFileResult = new UploadFileResult();
        UploadFileInfo uploadFileInfo = JSON.parseObject(redisUtil.getObject("QiwenUploader:Identifier:" + uploadFile.getIdentifier() + ":uploadPartRequest"), UploadFileInfo.class);

        uploadFileResult.setFileUrl(uploadFileInfo.getKey());
        uploadFileResult.setFileName(qiwenMultipartFile.getFileName());
        uploadFileResult.setExtendName(qiwenMultipartFile.getExtendName());
        uploadFileResult.setFileSize(uploadFile.getTotalSize());
        if (uploadFile.getTotalChunks() == 1) {
            uploadFileResult.setFileSize(qiwenMultipartFile.getSize());
        }
        uploadFileResult.setStorageType(StorageTypeEnum.ALIYUN_OSS);

        if (uploadFile.getChunkNumber() == uploadFile.getTotalChunks()) {
            log.info("分片上传完成");
            completeMultipartUpload(uploadFile);
            redisUtil.deleteKey("QiwenUploader:Identifier:" + uploadFile.getIdentifier() + ":current_upload_chunk_number");
            redisUtil.deleteKey("QiwenUploader:Identifier:" + uploadFile.getIdentifier() + ":partETags");
            redisUtil.deleteKey("QiwenUploader:Identifier:" + uploadFile.getIdentifier() + ":uploadPartRequest");

            uploadFileResult.setStatus(UploadFileStatusEnum.SUCCESS);
        } else {
            uploadFileResult.setStatus(UploadFileStatusEnum.UNCOMPLATE);

        }
        return uploadFileResult;
    }


    /**
     * 将文件分块进行升序排序并执行文件上传。
     * @param uploadFile 上传信息
     */
    private void completeMultipartUpload(UploadFile uploadFile) {

        List<PartETag> partETags = JSON.parseArray(redisUtil.getObject("QiwenUploader:Identifier:" + uploadFile.getIdentifier() + ":partETags"), PartETag.class);

        Collections.sort(partETags, Comparator.comparingInt(PartETag::getPartNumber));

        UploadFileInfo uploadFileInfo = JSON.parseObject(redisUtil.getObject("QiwenUploader:Identifier:" + uploadFile.getIdentifier() + ":uploadPartRequest"), UploadFileInfo.class);

        CompleteMultipartUploadRequest completeMultipartUploadRequest =
                new CompleteMultipartUploadRequest(aliyunConfig.getOss().getBucketName(),
                        uploadFileInfo.getKey(),
                        uploadFileInfo.getUploadId(),
                        partETags);
        OSS ossClient = AliyunUtils.getOSSClient(aliyunConfig);
        // 完成上传。
        ossClient.completeMultipartUpload(completeMultipartUploadRequest);
        ossClient.shutdown();

    }

    /**
     * 取消上传
     */
    @Override
    public void cancelUpload(UploadFile uploadFile) {

        UploadFileInfo uploadFileInfo = JSON.parseObject(redisUtil.getObject("QiwenUploader:Identifier:" + uploadFile.getIdentifier() + ":uploadPartRequest"), UploadFileInfo.class);

        OSS ossClient = AliyunUtils.getOSSClient(aliyunConfig);
        AbortMultipartUploadRequest abortMultipartUploadRequest =
                new AbortMultipartUploadRequest(aliyunConfig.getOss().getBucketName(),
                        uploadFileInfo.getKey(),
                        uploadFileInfo.getUploadId());
        ossClient.abortMultipartUpload(abortMultipartUploadRequest);
    }


}
