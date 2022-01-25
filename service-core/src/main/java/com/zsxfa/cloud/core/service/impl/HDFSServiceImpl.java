package com.zsxfa.cloud.core.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.zsxfa.cloud.base.util.UFOPUtils;
import com.zsxfa.cloud.core.component.FileDealComp;
import com.zsxfa.cloud.core.config.fileConf.constant.UploadFileStatusEnum;
import com.zsxfa.cloud.core.config.fileConf.exception.DeleteException;
import com.zsxfa.cloud.core.config.fileConf.exception.UploadException;
import com.zsxfa.cloud.core.config.fileConf.operation.download.domain.DownloadFile;
import com.zsxfa.cloud.core.config.fileConf.operation.download.product.LocalStorageDownloader;
import com.zsxfa.cloud.core.config.fileConf.operation.preview.Previewer;
import com.zsxfa.cloud.core.config.fileConf.operation.preview.domain.PreviewFile;
import com.zsxfa.cloud.core.config.fileConf.operation.preview.product.LocalStoragePreviewer;
import com.zsxfa.cloud.core.config.fileConf.operation.upload.domain.UploadFile;
import com.zsxfa.cloud.core.config.fileConf.operation.upload.domain.UploadFileResult;
import com.zsxfa.cloud.core.config.fileConf.operation.upload.product.LocalStorageUploader;
import com.zsxfa.cloud.core.mapper.*;
import com.zsxfa.cloud.core.pojo.dto.file.DownloadFileDTO;
import com.zsxfa.cloud.core.pojo.dto.file.PreviewDTO;
import com.zsxfa.cloud.core.pojo.dto.file.UploadFileDTO;
import com.zsxfa.cloud.core.pojo.entity.File;
import com.zsxfa.cloud.core.pojo.entity.UploadTask;
import com.zsxfa.cloud.core.pojo.entity.UploadTaskDetail;
import com.zsxfa.cloud.core.pojo.entity.UserFile;
import com.zsxfa.cloud.core.pojo.vo.FileListVo;
import com.zsxfa.cloud.core.service.HDFSService;
import com.zsxfa.common.util.DateUtil;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.List;
import java.util.zip.Adler32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author zsxfa
 */
@Service
@Transactional(rollbackFor=Exception.class)
public class HDFSServiceImpl implements HDFSService {

    private static final String HDFS_URI = "hdfs://192.168.138.102:9000";
    private static final String HDFS_NAME = "jachin";

    @Resource
    FileMapper fileMapper;
    @Resource
    UserFileMapper userFileMapper;
    @Resource
    FileDealComp fileDealComp;
    @Resource
    UploadTaskDetailMapper UploadTaskDetailMapper;
    @Resource
    UploadTaskMapper uploadTaskMapper;

    //hdfs访问核心
    FileSystem fileSystem = getFileSystem();
    FSDataOutputStream fsDataOutputStream = null;
    BufferedOutputStream out = null;

    public static Configuration getConfiguration(){
        Configuration configuration = new Configuration();
        configuration.set("fs.defaultFS", HDFS_URI);
        return configuration;
    }
    /**
     * 获取 HDFS 文件系统对象
     */
    public static FileSystem getFileSystem() {
        System.setProperty("HADOOP_USER_NAME", HDFS_NAME);
        FileSystem fs = null;
        try {
            fs = FileSystem.get(URI.create(HDFS_URI), getConfiguration());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return fs;
    }
    /**
     * 上传文件
     */
    @Override
    public void uploadFile(HttpServletRequest request, UploadFileDTO uploadFileDto, Long userId) {
        UploadFile uploadFile = new UploadFile();
        uploadFile.setChunkNumber(uploadFileDto.getChunkNumber());
        uploadFile.setChunkSize(uploadFileDto.getChunkSize());
        uploadFile.setTotalChunks(uploadFileDto.getTotalChunks());
        uploadFile.setIdentifier(uploadFileDto.getIdentifier());
        uploadFile.setTotalSize(uploadFileDto.getTotalSize());
        uploadFile.setCurrentChunkSize(uploadFileDto.getCurrentChunkSize());
        LocalStorageUploader uploader = new LocalStorageUploader();

        System.out.println("===============从HDFSServiceImpl进入upload方法=============");
        List<UploadFileResult> uploadFileResultList = uploader.upload(request, uploadFile);

        for (int i = 0; i < uploadFileResultList.size(); i++){
            UploadFileResult uploadFileResult = uploadFileResultList.get(i);
            File fileBean = new File();

            BeanUtil.copyProperties(uploadFileDto, fileBean);
            String relativePath = uploadFileDto.getRelativePath();

            if (UploadFileStatusEnum.SUCCESS.equals(uploadFileResult.getStatus())){
                fileBean.setFileUrl(uploadFileResult.getFileUrl());
                fileBean.setFileSize(uploadFileResult.getFileSize());
                fileBean.setStorageType(uploadFileResult.getStorageType().getCode());
                fileBean.setPointCount(1);
                fileMapper.insert(fileBean); //插入数据库
                UserFile userFile = new UserFile();

                if (relativePath.contains("/")) {
                    userFile.setFilePath(uploadFileDto.getFilePath() + UFOPUtils.getParentPath(relativePath) + "/");
                    fileDealComp.restoreParentFilePath(uploadFileDto.getFilePath() + UFOPUtils.getParentPath(relativePath) + "/", userId);
                    fileDealComp.deleteRepeatSubDirFile(uploadFileDto.getFilePath(), userId);

                } else {
                    userFile.setFilePath(uploadFileDto.getFilePath());
                }

                userFile.setUserId(userId);
                userFile.setFileName(uploadFileResult.getFileName());
                userFile.setExtendName(uploadFileResult.getExtendName());
                userFile.setDeleteFlag(0);
                userFile.setIsDir(0);
                List<FileListVo> userFileList = userFileMapper.userFileList(userFile, null, null);
                if (userFileList.size() > 0) {
                    String fileName = fileDealComp.getRepeatFileName(userFile, uploadFileDto.getFilePath());
                    userFile.setFileName(fileName);
                }
                userFile.setFileId(fileBean.getFileId());
                userFile.setUploadTime(DateUtil.getCurrentTime());
                userFileMapper.insert(userFile);

                LambdaQueryWrapper<UploadTaskDetail> lambdaQueryWrapper = new LambdaQueryWrapper<>();
                lambdaQueryWrapper.eq(UploadTaskDetail::getIdentifier, uploadFileDto.getIdentifier());
                UploadTaskDetailMapper.delete(lambdaQueryWrapper);

                LambdaUpdateWrapper<UploadTask> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
                lambdaUpdateWrapper.set(UploadTask::getUploadStatus, UploadFileStatusEnum.SUCCESS.getCode())
                        .eq(UploadTask::getIdentifier, uploadFileDto.getIdentifier());
                uploadTaskMapper.update(null, lambdaUpdateWrapper);

            }
            else if (UploadFileStatusEnum.UNCOMPLATE.equals(uploadFileResult.getStatus())) {
                UploadTaskDetail UploadTaskDetail = new UploadTaskDetail();
                UploadTaskDetail.setFilePath(uploadFileDto.getFilePath());
                if (relativePath.contains("/")) {
                    UploadTaskDetail.setFilePath(uploadFileDto.getFilePath() + UFOPUtils.getParentPath(relativePath) + "/");
                } else {
                    UploadTaskDetail.setFilePath(uploadFileDto.getFilePath());
                }
                UploadTaskDetail.setFileName(uploadFileDto.getFilename());
                UploadTaskDetail.setChunkNumber(uploadFileDto.getChunkNumber());
                UploadTaskDetail.setChunkSize((int)uploadFileDto.getChunkSize());
                UploadTaskDetail.setRelativePath(uploadFileDto.getRelativePath());
                UploadTaskDetail.setTotalChunks(uploadFileDto.getTotalChunks());
                UploadTaskDetail.setTotalSize((int)uploadFileDto.getTotalSize());
                UploadTaskDetail.setIdentifier(uploadFileDto.getIdentifier());
                UploadTaskDetailMapper.insert(UploadTaskDetail);

            } else if (UploadFileStatusEnum.FAIL.equals(uploadFileResult.getStatus())) {
                LambdaQueryWrapper<UploadTaskDetail> lambdaQueryWrapper = new LambdaQueryWrapper<>();
                lambdaQueryWrapper.eq(UploadTaskDetail::getIdentifier, uploadFileDto.getIdentifier());
                UploadTaskDetailMapper.delete(lambdaQueryWrapper);

                LambdaUpdateWrapper<UploadTask> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
                lambdaUpdateWrapper.set(UploadTask::getUploadStatus, UploadFileStatusEnum.FAIL.getCode())
                        .eq(UploadTask::getIdentifier, uploadFileDto.getIdentifier());
                uploadTaskMapper.update(null, lambdaUpdateWrapper);
            }
        }
    }
    /**
     * 下载文件
     */
    @Override
    public void downloadFile(HttpServletResponse httpServletResponse, DownloadFileDTO downloadFileDTO) {
        UserFile userFile = userFileMapper.selectById(downloadFileDTO.getUserFileId());

        if (userFile.getIsDir() == 0) {
            File fileBean = fileMapper.selectById(userFile.getFileId());
            System.out.println("171表示是一个文件");
//            Downloader downloader = ufopFactory.getDownloader(fileBean.getStorageType());

            LocalStorageDownloader downloader = new LocalStorageDownloader();
            System.out.println("此时创建了一个Downloader对象");
            if (downloader == null) {
                throw new DeleteException("下载失败");
            }
            DownloadFile downloadFile = new DownloadFile();

            downloadFile.setFileUrl(fileBean.getFileUrl());
            downloadFile.setFileSize(fileBean.getFileSize());
            httpServletResponse.setContentLengthLong(fileBean.getFileSize());
            downloader.download(httpServletResponse, downloadFile);
        } else {//下载的是文件夹
            LambdaQueryWrapper<UserFile> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.likeRight(UserFile::getFilePath, userFile.getFilePath() + userFile.getFileName() + "/")
                    .eq(UserFile::getUserId, userFile.getUserId())
                    .eq(UserFile::getIsDir, 0)
                    .eq(UserFile::getDeleteFlag, 0);
            List<UserFile> userFileList = userFileMapper.selectList(lambdaQueryWrapper);

            //hdfs上的文件路径
//            String staticPath = UFOPUtils.getStaticPath();
//            String tempPath = staticPath + "temp" + java.io.File.separator;
//            String tempPath = userFile.getFilePath() + "upload/temp" + java.io.File.separator;
            String tempPath = userFile.getFilePath() + "upload/temp";
            System.out.println("209的tempPath是："+tempPath);
            LocalStorageDownloader downloader = null;
            try {
                if(!fileSystem.exists(new Path(tempPath))){
                    fileSystem.mkdirs(new Path(tempPath));
                    System.out.println("223在hdfs上创建了temp文件夹");
                }
                fsDataOutputStream = fileSystem.create(new Path(tempPath + "/" + userFile.getFileName() + ".zip"));
                System.out.println("226hdfs上的文件路径是："+tempPath + "/" + userFile.getFileName() + ".zip");
                CheckedOutputStream csum = new CheckedOutputStream(fsDataOutputStream, new Adler32());
                ZipOutputStream zos = new ZipOutputStream(csum);
                out = new BufferedOutputStream(zos);

                downloader = new LocalStorageDownloader();
                for (UserFile userFile1 : userFileList) {
                    File fileBean = fileMapper.selectById(userFile1.getFileId());
                    DownloadFile downloadFile = new DownloadFile();
                    downloadFile.setFileUrl(fileBean.getFileUrl());
                    downloadFile.setFileSize(fileBean.getFileSize());
                    InputStream inputStream = downloader.getInputStream(downloadFile);
                    BufferedInputStream bis = new BufferedInputStream(inputStream);
                    try {
                        zos.putNextEntry(new ZipEntry(userFile1.getFilePath().replace(userFile.getFilePath(), "/") + userFile1.getFileName() + "." + userFile1.getExtendName()));
                        byte[] buffer = new byte[1024];
                        int i = bis.read(buffer);
                        while (i != -1) {
                            out.write(buffer, 0, i);
                            i = bis.read(buffer);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            bis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            out.flush();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    fsDataOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            System.out.println("276压缩完成！！！");

            DownloadFile downloadFile = new DownloadFile();
//            downloadFile.setFileUrl("/upload/temp/" + java.io.File.separator+userFile.getFileName() + ".zip");
            downloadFile.setFileUrl("upload/temp/" + userFile.getFileName() + ".zip");
            System.out.println("267行的downLoadFile的路径是："+downloadFile.getFileUrl());

            try {
                FileStatus fileStatus = fileSystem.getFileStatus(new Path("/" + downloadFile.getFileUrl()));
                httpServletResponse.setContentLengthLong(fileStatus.getLen());
                System.out.println("273的fileStatus的长度是："+fileStatus.getLen());
                System.out.println("273的fileStatus的路径是："+fileStatus.getPath());
                System.out.println("273的fileStatus的BlockSize是："+fileStatus.getBlockSize());

                downloader.download(httpServletResponse, downloadFile);
//            String zipPath = UFOPUtils.getStaticPath() + "temp" + java.io.File.separator+userFile.getFileName() + ".zip";
//                String zipPath = UFOPUtils.getStaticPath() + "temp" + userFile.getFileName() + ".zip";
//            java.io.File file = new java.io.File(zipPath);
                System.out.println("278下面是删除路径："+"/upload/temp/" + userFile.getFileName() + ".zip");
                if (fileSystem.exists(new Path("/upload/temp/" + userFile.getFileName() + ".zip"))) {
                    fileSystem.delete(new Path("/upload/temp/" + userFile.getFileName() + ".zip"));
                    System.out.println("280 hdfs上的临时文件已经删除了");
                }
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * 预览文件
     */
    @Override
    public void previewFile(HttpServletResponse httpServletResponse, PreviewDTO previewDTO) {
        UserFile userFile = userFileMapper.selectById(previewDTO.getUserFileId());
        File fileBean = fileMapper.selectById(userFile.getFileId());
        LocalStoragePreviewer previewer = new LocalStoragePreviewer();

        PreviewFile previewFile = new PreviewFile();
        previewFile.setFileUrl(fileBean.getFileUrl());
        previewFile.setFileSize(fileBean.getFileSize());
        if ("true".equals(previewDTO.getIsMin())) {
            previewer.imageThumbnailPreview(httpServletResponse, previewFile);
        } else {
            previewer.imageOriginalPreview(httpServletResponse, previewFile);
        }
    }
}
