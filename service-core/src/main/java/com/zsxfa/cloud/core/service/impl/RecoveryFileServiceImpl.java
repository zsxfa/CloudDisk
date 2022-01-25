package com.zsxfa.cloud.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.zsxfa.cloud.core.component.FileDealComp;
import com.zsxfa.cloud.core.config.fileConf.exception.UploadException;
import com.zsxfa.cloud.core.mapper.FileMapper;
import com.zsxfa.cloud.core.mapper.UserFileMapper;
import com.zsxfa.cloud.core.pojo.entity.File;
import com.zsxfa.cloud.core.pojo.entity.RecoveryFile;
import com.zsxfa.cloud.core.mapper.RecoveryFileMapper;
import com.zsxfa.cloud.core.pojo.entity.UserFile;
import com.zsxfa.cloud.core.pojo.vo.RecoveryFileListVo;
import com.zsxfa.cloud.core.service.RecoveryFileService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zsxfa
 */
@Service
@Transactional(rollbackFor=Exception.class)
public class RecoveryFileServiceImpl extends ServiceImpl<RecoveryFileMapper, RecoveryFile> implements RecoveryFileService {

    @Resource
    RecoveryFileMapper recoveryFileMapper;
    @Resource
    UserFileMapper userFileMapper;
    @Resource
    FileMapper fileMapper;
    @Resource
    FileDealComp fileDealComp;

    public static Executor executor = Executors.newFixedThreadPool(20);

    @Override
    public List<RecoveryFileListVo> selectRecoveryFileList(Long userId) {
        return recoveryFileMapper.selectRecoveryFileList(userId);
    }

    @Override
    public void restorefile(String deleteBatchNum, String filePath, Long sessionUserId) {
        LambdaUpdateWrapper<UserFile> userFileLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        userFileLambdaUpdateWrapper.set(UserFile::getDeleteFlag, 0)
                .set(UserFile::getDeleteBatchNum, "")
                .eq(UserFile::getDeleteBatchNum, deleteBatchNum);
        userFileMapper.update(null, userFileLambdaUpdateWrapper);

        fileDealComp.restoreParentFilePath(filePath, sessionUserId);

        fileDealComp.deleteRepeatSubDirFile(filePath, sessionUserId);
        // TODO 如果被还原的文件已存在，暂未实现
        LambdaQueryWrapper<RecoveryFile> recoveryFileServiceLambdaQueryWrapper = new LambdaQueryWrapper<>();
        recoveryFileServiceLambdaQueryWrapper.eq(RecoveryFile::getDeleteBatchNum, deleteBatchNum);
        recoveryFileMapper.delete(recoveryFileServiceLambdaQueryWrapper);
    }

    @Override
    public void deleteRecoveryFile(UserFile userFile) {
        if (userFile == null) {
            return ;
        }
        try {
            if (userFile.getIsDir() == 1) {
                updateFilePointCountByBatchNum(userFile.getDeleteBatchNum());

            }else{//单文件
                UserFile userFileTemp = userFileMapper.selectById(userFile.getUserFileId());
                File fileBean = fileMapper.selectById(userFileTemp.getFileId());

                LambdaUpdateWrapper<File> fileBeanLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
                fileBeanLambdaUpdateWrapper.set(File::getPointCount, fileBean.getPointCount() -1)
                        .eq(File::getFileId, fileBean.getFileId());

                fileMapper.update(null, fileBeanLambdaUpdateWrapper);

                //再次获取更新后的信息
                fileBean = fileMapper.selectById(userFileTemp.getFileId());
                if(fileBean.getPointCount() == 0){//没有引用的时候就需要删除这条记录
                    //删除hdfs上的数据
                    FileSystem fileSystem = HDFSServiceImpl.getFileSystem();
                    Path hdfsPath = new Path("/"+fileBean.getFileUrl());
                    if (fileSystem.exists(hdfsPath)){
                        fileSystem.delete(hdfsPath, true);
                    }
                    fileBeanLambdaUpdateWrapper.eq(File::getIdentifier, fileBean.getIdentifier());
                    fileMapper.delete(fileBeanLambdaUpdateWrapper);
                }
            }
            //删除数据库
            LambdaQueryWrapper<UserFile> userFileLambdaQueryWrapper = new LambdaQueryWrapper<>();
            userFileLambdaQueryWrapper.eq(UserFile::getDeleteBatchNum, userFile.getDeleteBatchNum());
            userFileMapper.delete(userFileLambdaQueryWrapper);
        }catch (IOException e) {
            throw new UploadException(e);
        }


    }
    private void updateFilePointCountByBatchNum(String deleteBatchNum) {
        LambdaQueryWrapper<UserFile> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserFile::getDeleteBatchNum, deleteBatchNum);
        List<UserFile> fileList = userFileMapper.selectList(lambdaQueryWrapper);

        //获取hdfs实例对象
        FileSystem fileSystem = HDFSServiceImpl.getFileSystem();

        new Thread(()->{
            for (int i = 0; i < fileList.size(); i++){
                UserFile userFileTemp = fileList.get(i);
                executor.execute(() -> {
                    if (userFileTemp.getIsDir() != 1){
                        File fileBean = fileMapper.selectById(userFileTemp.getFileId());
                        if (fileBean.getPointCount() != null) {

                            LambdaUpdateWrapper<File> fileBeanLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
                            fileBeanLambdaUpdateWrapper.set(File::getPointCount, fileBean.getPointCount() -1)
                                    .eq(File::getFileId, fileBean.getFileId());
                            fileMapper.update(null, fileBeanLambdaUpdateWrapper);

                            //再次获取更新后的信息
                            fileBean = fileMapper.selectById(userFileTemp.getFileId());
                            if(fileBean.getPointCount() == 0){
                                //删除逻辑
                                try {
                                    Path hdfsPath = new Path("/"+fileBean.getFileUrl());
                                    if (fileSystem.exists(hdfsPath)){
                                        boolean delete = fileSystem.delete(hdfsPath, true);
                                        if(delete){
                                            System.out.println("已经从hdfs上删除了文件："+ userFileTemp.getFileName());
                                        }
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                fileBeanLambdaUpdateWrapper.eq(File::getIdentifier, fileBean.getIdentifier());
                                fileMapper.delete(fileBeanLambdaUpdateWrapper);

                            }
                        }
                    }
                });
            }
        }).start();
    }


}
