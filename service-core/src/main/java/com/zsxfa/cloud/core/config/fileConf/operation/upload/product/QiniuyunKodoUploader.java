package com.zsxfa.cloud.core.config.fileConf.operation.upload.product;

import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.storage.persistent.FileRecorder;
import com.qiniu.util.Auth;
import com.zsxfa.cloud.core.config.fileConf.config.QiniuyunConfig;
import com.zsxfa.cloud.core.config.fileConf.constant.StorageTypeEnum;
import com.zsxfa.cloud.core.config.fileConf.constant.UploadFileStatusEnum;
import com.zsxfa.cloud.core.config.fileConf.exception.UFOPException;
import com.zsxfa.cloud.core.config.fileConf.exception.UploadException;
import com.zsxfa.cloud.core.config.fileConf.operation.upload.Uploader;
import com.zsxfa.cloud.core.config.fileConf.operation.upload.domain.UploadFile;
import com.zsxfa.cloud.core.config.fileConf.operation.upload.domain.UploadFileResult;
import com.zsxfa.cloud.core.config.fileConf.operation.upload.request.QiwenMultipartFile;
import com.zsxfa.cloud.core.config.fileConf.util.QiniuyunUtils;
import com.zsxfa.cloud.core.config.fileConf.util.RedisUtil;
import com.zsxfa.cloud.core.config.fileConf.util.UFOPUtils;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;

@Slf4j
public class QiniuyunKodoUploader extends Uploader {

    private QiniuyunConfig qiniuyunConfig;

    @Resource
    RedisUtil redisUtil;

    public QiniuyunKodoUploader(){

    }

    public QiniuyunKodoUploader(QiniuyunConfig qiniuyunConfig){
        this.qiniuyunConfig = qiniuyunConfig;
    }

    @Override
    public void cancelUpload(UploadFile uploadFile) {

    }

    @Override
    protected void doUploadFileChunk(QiwenMultipartFile qiwenMultipartFile, UploadFile uploadFile) {

    }

    @Override
    protected UploadFileResult organizationalResults(QiwenMultipartFile qiwenMultipartFile, UploadFile uploadFile) {
        return null;
    }

    @Override
    protected UploadFileResult doUploadFlow(QiwenMultipartFile qiwenMultipartFile, UploadFile uploadFile) {
        UploadFileResult uploadFileResult = new UploadFileResult();
        try {
            qiwenMultipartFile.getFileUrl(uploadFile.getIdentifier());
            String fileUrl = UFOPUtils.getUploadFileUrl(uploadFile.getIdentifier(), qiwenMultipartFile.getExtendName());

            File tempFile =  UFOPUtils.getTempFile(fileUrl);
            File processFile = UFOPUtils.getProcessFile(fileUrl);

            byte[] fileData = qiwenMultipartFile.getUploadBytes();

            writeByteDataToFile(fileData, tempFile, uploadFile);

            //????????????????????????????????????????????????????????????
            boolean isComplete = checkUploadStatus(uploadFile, processFile);
            uploadFileResult.setFileUrl(fileUrl);
            uploadFileResult.setFileName(qiwenMultipartFile.getFileName());
            uploadFileResult.setExtendName(qiwenMultipartFile.getExtendName());
            uploadFileResult.setFileSize(uploadFile.getTotalSize());
            uploadFileResult.setStorageType(StorageTypeEnum.QINIUYUN_KODO);

            if (uploadFile.getTotalChunks() == 1) {
                uploadFileResult.setFileSize(qiwenMultipartFile.getSize());
            }

            if (isComplete) {

                qiniuUpload(fileUrl, tempFile, uploadFile);
                uploadFileResult.setFileUrl(fileUrl);
                boolean result = tempFile.delete();
                if (!result) {
                    throw new UFOPException("??????temp??????????????????????????????"+ tempFile.getPath());
                }
                uploadFileResult.setStatus(UploadFileStatusEnum.SUCCESS);
            } else {
                uploadFileResult.setStatus(UploadFileStatusEnum.UNCOMPLATE);
            }
        } catch (IOException e) {
            throw new UploadException(e);
        }


        return uploadFileResult;
    }


    private void qiniuUpload(String fileUrl, File file,  UploadFile uploadFile) {
        Configuration cfg = QiniuyunUtils.getCfg(qiniuyunConfig);
        cfg.resumableUploadAPIVersion = Configuration.ResumableUploadAPIVersion.V2;// ????????????????????????
        cfg.resumableUploadMaxConcurrentTaskCount = 2;  // ???????????????????????????1??????????????????????????????1?????????????????????
//...???????????????????????????

//...???????????????????????????????????????



        Auth auth = Auth.create(qiniuyunConfig.getKodo().getAccessKey(), qiniuyunConfig.getKodo().getSecretKey());
        String upToken = auth.uploadToken(qiniuyunConfig.getKodo().getBucketName());

        String localTempDir = UFOPUtils.getStaticPath() + "temp";
        try {
            //??????????????????????????????????????????
            FileRecorder fileRecorder = new FileRecorder(localTempDir);
            UploadManager uploadManager = new UploadManager(cfg, fileRecorder);
            try {
                Response response = uploadManager.put(file.getAbsoluteFile(), fileUrl, upToken);
                //???????????????????????????
                DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
                log.info(putRet.key);
                log.info(putRet.hash);
            } catch (QiniuException ex) {
                Response r = ex.response;
                System.err.println(r.toString());
                try {
                    System.err.println(r.bodyString());
                } catch (QiniuException ex2) {
                    //ignore
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }


    }


}
