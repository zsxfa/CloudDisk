package com.zsxfa.cloud.core.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.qiwenshare.common.util.DateUtil;
import com.zsxfa.cloud.core.component.FileDealComp;
import com.zsxfa.cloud.core.config.fileConf.constant.StorageTypeEnum;
import com.zsxfa.cloud.core.config.fileConf.constant.UploadFileStatusEnum;
import com.zsxfa.cloud.core.config.fileConf.exception.DeleteException;
import com.zsxfa.cloud.core.config.fileConf.exception.UploadException;
import com.zsxfa.cloud.core.config.fileConf.factory.UFOPFactory;
import com.zsxfa.cloud.core.config.fileConf.operation.download.Downloader;
import com.zsxfa.cloud.core.config.fileConf.operation.download.domain.DownloadFile;
import com.zsxfa.cloud.core.config.fileConf.operation.download.product.LocalStorageDownloader;
import com.zsxfa.cloud.core.config.fileConf.operation.upload.Uploader;
import com.zsxfa.cloud.core.config.fileConf.operation.upload.domain.UploadFile;
import com.zsxfa.cloud.core.config.fileConf.operation.upload.domain.UploadFileResult;
import com.zsxfa.cloud.core.config.fileConf.operation.upload.product.LocalStorageUploader;
import com.zsxfa.cloud.core.config.fileConf.util.UFOPUtils;
import com.zsxfa.cloud.core.mapper.FileMapper;
import com.zsxfa.cloud.core.mapper.UploadTaskDetailMapper;
import com.zsxfa.cloud.core.mapper.UploadTaskMapper;
import com.zsxfa.cloud.core.mapper.UserFileMapper;
import com.zsxfa.cloud.core.pojo.dto.file.DownloadFileDTO;
import com.zsxfa.cloud.core.pojo.dto.file.PreviewDTO;
import com.zsxfa.cloud.core.pojo.dto.file.UploadFileDTO;
import com.zsxfa.cloud.core.pojo.entity.File;
import com.zsxfa.cloud.core.pojo.entity.UploadTask;
import com.zsxfa.cloud.core.pojo.entity.UploadTaskDetail;
import com.zsxfa.cloud.core.pojo.entity.UserFile;
import com.zsxfa.cloud.core.pojo.vo.FileListVo;
import com.zsxfa.cloud.core.service.FiletransferService;
import com.zsxfa.common.exception.DefinedException;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.ipc.RemoteException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;
import java.util.zip.Adler32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import static com.zsxfa.cloud.core.config.fileConf.operation.upload.Uploader.FILE_SEPARATOR;
import static com.zsxfa.cloud.core.config.fileConf.operation.upload.Uploader.ROOT_PATH;

/**
 * @author zsxfa
 */
@Slf4j
@Service
@Transactional(rollbackFor=Exception.class)
public class FiletransferServiceImpl implements FiletransferService {

    @Resource
    UserFileMapper userFileMapper;

    @Override
    public Long selectStorageSizeByUserId(Long userId) {
        return userFileMapper.selectStorageSizeByUserId(userId);
    }

}
