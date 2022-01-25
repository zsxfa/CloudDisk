package com.zsxfa.cloud.core.config.fileConf.operation.upload.product;

import com.zsxfa.cloud.core.config.fileConf.constant.StorageTypeEnum;
import com.zsxfa.cloud.core.config.fileConf.constant.UploadFileStatusEnum;
import com.zsxfa.cloud.core.config.fileConf.exception.UploadException;
import com.zsxfa.cloud.core.config.fileConf.operation.upload.Uploader;
import com.zsxfa.cloud.core.config.fileConf.operation.upload.domain.UploadFile;
import com.zsxfa.cloud.core.config.fileConf.operation.upload.domain.UploadFileResult;
import com.zsxfa.cloud.core.config.fileConf.operation.upload.request.QiwenMultipartFile;
import com.zsxfa.cloud.core.config.fileConf.util.UFOPUtils;
import com.zsxfa.cloud.core.service.impl.HDFSServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Map;

@Component
public class LocalStorageUploader extends Uploader {

    public static Map<String, String> FILE_URL_MAP = new HashMap<>();


    @Override
    protected UploadFileResult doUploadFlow(QiwenMultipartFile qiwenMultipartFile, UploadFile uploadFile) {
        UploadFileResult uploadFileResult = new UploadFileResult();
        FSDataOutputStream fsDataOutputStream = null;
        try {
            String fileUrl = UFOPUtils.getUploadFileUrl(uploadFile.getIdentifier(), qiwenMultipartFile.getExtendName());
            if (StringUtils.isNotEmpty(FILE_URL_MAP.get(uploadFile.getIdentifier()))) {
                fileUrl = FILE_URL_MAP.get(uploadFile.getIdentifier());
            } else {
                FILE_URL_MAP.put(uploadFile.getIdentifier(), fileUrl);
            }
            String tempFileUrl = fileUrl + "_tmp";
            String confFileUrl = fileUrl.replace("." + qiwenMultipartFile.getExtendName(), ".conf");

            File file = new File(UFOPUtils.getStaticPath() + fileUrl);
            File tempFile = new File(UFOPUtils.getStaticPath() + tempFileUrl);
            File confFile = new File(UFOPUtils.getStaticPath() + confFileUrl);

            //hdfs上传
            InputStream uploadInputStream = qiwenMultipartFile.getUploadInputStream();
            FileSystem fileSystem = HDFSServiceImpl.getFileSystem();
            System.out.println("fileUrl: "+ fileUrl);
            fsDataOutputStream = fileSystem.create(new Path("/"+fileUrl));
            IOUtils.copyBytes(uploadInputStream, fsDataOutputStream, 4096, true);

//            //第一步 打开将要写入的文件
//            RandomAccessFile raf = new RandomAccessFile(tempFile, "rw");
//            //第二步 打开通道
//            FileChannel fileChannel = raf.getChannel();
//            //第三步 计算偏移量
//            long position = (uploadFile.getChunkNumber() - 1) * uploadFile.getChunkSize();
//            //第四步 获取分片数据
//            byte[] fileData = qiwenMultipartFile.getUploadBytes();
//            //第五步 写入数据
//            fileChannel.position(position);
//            fileChannel.write(ByteBuffer.wrap(fileData));
//            fileChannel.force(true);
//            fileChannel.close();
//            raf.close();

            confFile.delete();
            //判断是否完成文件的传输并进行校验与重命名
            boolean isComplete = checkUploadStatus(uploadFile, confFile);
            uploadFileResult.setFileUrl(fileUrl);
            uploadFileResult.setFileName(qiwenMultipartFile.getFileName());
            uploadFileResult.setExtendName(qiwenMultipartFile.getExtendName());
            uploadFileResult.setFileSize(uploadFile.getTotalSize());
            uploadFileResult.setStorageType(StorageTypeEnum.LOCAL);
            uploadFileResult.setStatus(UploadFileStatusEnum.SUCCESS);

            if (uploadFile.getTotalChunks() == 1) {
                uploadFileResult.setFileSize(qiwenMultipartFile.getSize());
            }

            if (isComplete) {
            confFile.delete();
                tempFile.renameTo(file);
                FILE_URL_MAP.remove(uploadFile.getIdentifier());
                uploadFileResult.setStatus(UploadFileStatusEnum.SUCCESS);
            } else {
                uploadFileResult.setStatus(UploadFileStatusEnum.UNCOMPLATE);
            }
        } catch (IOException e) {
            throw new UploadException(e);
        }

        return uploadFileResult;
    }

    @Override
    public void cancelUpload(UploadFile uploadFile) {
        // TODO
    }

    @Override
    protected void doUploadFileChunk(QiwenMultipartFile qiwenMultipartFile, UploadFile uploadFile) throws IOException {

    }

    @Override
    protected UploadFileResult organizationalResults(QiwenMultipartFile qiwenMultipartFile, UploadFile uploadFile) {
        return null;
    }

}
