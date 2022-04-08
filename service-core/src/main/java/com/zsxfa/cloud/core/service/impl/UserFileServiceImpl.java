package com.zsxfa.cloud.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zsxfa.cloud.base.util.UFOPUtils;
import com.zsxfa.cloud.core.mapper.RecoveryFileMapper;
import com.zsxfa.cloud.core.pojo.entity.RecoveryFile;
import com.zsxfa.cloud.core.pojo.entity.User;
import com.zsxfa.cloud.core.pojo.entity.UserFile;
import com.zsxfa.cloud.core.mapper.UserFileMapper;
import com.zsxfa.cloud.core.pojo.vo.FileListVo;
import com.zsxfa.cloud.core.service.UserFileService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zsxfa.common.util.DateUtil;
import com.zsxfa.common.util.RandomUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zsxfa
 */
@Slf4j
@Service
public class UserFileServiceImpl extends ServiceImpl<UserFileMapper, UserFile> implements UserFileService {

    @Resource
    UserFileMapper userFileMapper;
    @Resource
    RecoveryFileMapper recoveryFileMapper;

    public static Executor executor = Executors.newFixedThreadPool(20);



    @Override
    public List<FileListVo> userFileList(UserFile userFile, Long beginCount, Long pageCount) {
        return userFileMapper.userFileList(userFile, beginCount, pageCount);
    }

    @Override
    public List<FileListVo> adminFileList(Long beginCount, Long pageCount, Long userId) {
        return userFileMapper.adminFileList(beginCount, pageCount, userId);
    }

    @Override
    public List<FileListVo> userSearchFileList(UserFile userFile, Long beginCount, Long pageCount) {
        return userFileMapper.userSearchFileList(userFile, beginCount, pageCount);
    }

    /**
     *
     * @param fileNameList  文件扩展名的集合
     * @param beginCount
     * @param pageCount
     * @param userId
     * @return
     */
    @Override
    public List<FileListVo> selectFileNotInExtendNames(List<String> fileNameList, Long beginCount, Long pageCount, Long userId) {
        return userFileMapper.selectFileNotInExtendNames(fileNameList, beginCount, pageCount, userId);
    }
    @Override
    public Long selectCountNotInExtendNames(List<String> fileNameList, Long beginCount, Long pageCount, Long userId) {
        return userFileMapper.selectCountNotInExtendNames(fileNameList, beginCount, pageCount, userId);
    }

    @Override
    public List<FileListVo> selectFileByExtendName(List<String> fileNameList, Long beginCount, Long pageCount, Long userId) {
        return userFileMapper.selectFileByExtendName(fileNameList,beginCount, pageCount, userId);
    }
    @Override
    public Long selectCountByExtendName(List<String> fileNameList, Long beginCount, Long pageCount, Long userId) {
        return userFileMapper.selectCountByExtendName(fileNameList, beginCount, pageCount, userId);
    }

    @Override
    public void deleteUserFile(Long userFileId, Long sessionUserId) {
        UserFile userFile = userFileMapper.selectById(userFileId);
        String uuid = UUID.randomUUID().toString();
        if (userFile.getIsDir() == 1) {//是文件夹
            LambdaUpdateWrapper<UserFile> userFileLambdaUpdateWrapper = new LambdaUpdateWrapper<UserFile>();
            userFileLambdaUpdateWrapper.set(UserFile::getDeleteFlag, RandomUtils.getSixBitRandom())
                    .set(UserFile::getDeleteBatchNum, uuid)
                    .set(UserFile::getDeleteTime, DateUtil.getCurrentTime())
                    .eq(UserFile::getUserFileId, userFileId);
            userFileMapper.update(null, userFileLambdaUpdateWrapper);

            String filePath = userFile.getFilePath() + userFile.getFileName() + "/";
            updateFileDeleteStateByFilePath(filePath, uuid, sessionUserId);

        }else{
            UserFile userFileTemp = userFileMapper.selectById(userFileId);
            LambdaUpdateWrapper<UserFile> userFileLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            userFileLambdaUpdateWrapper.set(UserFile::getDeleteFlag, RandomUtils.getSixBitRandom())
                    .set(UserFile::getDeleteTime, DateUtil.getCurrentTime())
                    .set(UserFile::getDeleteBatchNum, uuid)
                    .eq(UserFile::getUserFileId, userFileTemp.getUserFileId());
            userFileMapper.update(null, userFileLambdaUpdateWrapper);
        }

        RecoveryFile recoveryFile = new RecoveryFile();
        recoveryFile.setUserFileId(userFileId);
        recoveryFile.setDeleteTime(DateUtil.getCurrentTime());
        recoveryFile.setDeleteBatchNum(uuid);
        recoveryFileMapper.insert(recoveryFile);
    }
    /**
     * 删除文件夹中的文件
     * @param filePath
     * @param deleteBatchNum
     * @param userId
     */
    private void updateFileDeleteStateByFilePath(String filePath, String deleteBatchNum, Long userId) {
        executor.execute(() -> {
            List<UserFile> fileList = selectFileListLikeRightFilePath(filePath, userId);
            for (int i = 0; i < fileList.size(); i++){
                UserFile userFileTemp = fileList.get(i);
                //标记删除标志
                LambdaUpdateWrapper<UserFile> userFileLambdaUpdateWrapper1 = new LambdaUpdateWrapper<>();
                userFileLambdaUpdateWrapper1.set(UserFile::getDeleteFlag, RandomUtils.getSixBitRandom())
                        .set(UserFile::getDeleteTime, DateUtil.getCurrentTime())
                        .set(UserFile::getDeleteBatchNum, deleteBatchNum)
                        .eq(UserFile::getUserFileId, userFileTemp.getUserFileId())
                        .eq(UserFile::getDeleteFlag, 0);
                userFileMapper.update(null, userFileLambdaUpdateWrapper1);

            }
        });
    }
    @Override
    public List<UserFile> selectFileListLikeRightFilePath(String filePath, long userId) {
        log.info("查询文件路径1：" + filePath);
//        filePath = filePath.replace("\\", "\\\\\\\\");
//        filePath = filePath.replace("'", "\\'");
//        filePath = filePath.replace("%", "\\%");
//        filePath = filePath.replace("_", "\\_");

        LambdaQueryWrapper<UserFile> lambdaQueryWrapper = new LambdaQueryWrapper<>();

        log.info("查询文件路径2：" + filePath);

        lambdaQueryWrapper.eq(UserFile::getUserId, userId)
                .likeRight(UserFile::getFilePath, filePath)
                .eq(UserFile::getDeleteFlag, 0);
        return userFileMapper.selectList(lambdaQueryWrapper);
    }

    @Override
    public boolean isDirExist(String fileName, String filePath, Long userId) {
        LambdaQueryWrapper<UserFile> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserFile::getFileName, fileName)
                .eq(UserFile::getFilePath, filePath)
                .eq(UserFile::getUserId, userId)
                .eq(UserFile::getDeleteFlag, 0)
                .eq(UserFile::getIsDir, 1);
        List<UserFile> list = userFileMapper.selectList(lambdaQueryWrapper);
        if (list != null && !list.isEmpty()) {
            return true;
        }
        return false;
    }

    @Override
    public void updateFilepathByFilepath(String oldfilePath, String newfilePath, String fileName, String extendName, Long userId) {
        if ("null".equals(extendName)){
            extendName = null;
        }
        //移动根目录
        userFileMapper.updateFilepathByPathAndName(oldfilePath, newfilePath, fileName, extendName, userId);

        //移动子目录
        oldfilePath = oldfilePath + fileName + "/";
        newfilePath = newfilePath + fileName + "/";

        System.out.println("oldfilePath: " + oldfilePath);
        if (extendName == null) { //为null说明是目录，则需要移动子目录
            userFileMapper.updateFilepathByFilepath(oldfilePath, newfilePath, userId);
        }
    }

    @Override
    public List<UserFile> selectFilePathTreeByUserId(Long userId) {
        LambdaQueryWrapper<UserFile> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserFile::getUserId, userId)
                .eq(UserFile::getIsDir, 1)
                .eq(UserFile::getDeleteFlag, 0);
        return userFileMapper.selectList(lambdaQueryWrapper);
    }

    @Override
    public List<UserFile> selectUserFileByNameAndPath(String fileName, String filePath, Long userId) {
        LambdaQueryWrapper<UserFile> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserFile::getFileName, fileName)
                .eq(UserFile::getFilePath, filePath)
                .eq(UserFile::getUserId, userId)
                .eq(UserFile::getDeleteFlag, 0);
        return userFileMapper.selectList(lambdaQueryWrapper);
    }

    @Override
    public void replaceUserFilePath(String filePath, String oldFilePath, Long userId) {
        userFileMapper.replaceFilePath(filePath, oldFilePath, userId);
    }

    @Override
    public User getUserInfo(Long userId, Long fileId) {
        return userFileMapper.getUserInfo(userId, fileId);
    }

    @Override
    public Page<UserFile> searchFileList(Long page, Long limit, String fileName, Long userId) {
        LambdaQueryWrapper<UserFile> userLogLambdaQueryWrapper = new LambdaQueryWrapper<>();
        if(StringUtils.isNotBlank(fileName)){
            userLogLambdaQueryWrapper.like(UserFile::getFileName, fileName);
        }
        if(userId != null){
            userLogLambdaQueryWrapper.eq(UserFile::getUserId, userId);
        }
        userLogLambdaQueryWrapper.eq(UserFile::getIsDir,0);
        System.out.println(page);
        System.out.println(limit);
        Page<UserFile> p = new Page<>(page,limit);
        return baseMapper.selectPage(p, userLogLambdaQueryWrapper);
    }

    @Override
    public List<FileListVo> adminsSelectFileNotInExtendNames(List<String> fileNameList, Long page, Long limit, Long userId) {
        return userFileMapper.adminSelectFileNotInExtendNames(fileNameList, page, limit, userId);
    }
    @Override
    public Long adminSelectCountNotInExtendNames(List<String> fileNameList, Long page, Long limit, Long userId) {
        return userFileMapper.adminSelectCountNotInExtendNames(fileNameList, page, limit, userId);

    }

    @Override
    public List<FileListVo> adminSelectFileByExtendName(List<String> fileNameList, Long page, Long limit, Long userId) {
        return userFileMapper.adminSelectFileByExtendName(fileNameList, page, limit, userId);
    }
    @Override
    public Long adminSelectCountByExtendName(List<String> fileNameList, Long page, Long limit, Long userId) {
        return userFileMapper.adminSelectCountByExtendName(fileNameList, page, limit, userId);
    }

    @Override
    public List<UserFile> selectSameUserFile(String fileName, String filePath, String extendName, Long userId) {
        LambdaQueryWrapper<UserFile> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserFile::getFileName, fileName)
                .eq(UserFile::getFilePath, filePath)
                .eq(UserFile::getUserId, userId)
                .eq(UserFile::getExtendName, extendName)
                .eq(UserFile::getDeleteFlag, "0");
        return userFileMapper.selectList(lambdaQueryWrapper);
    }


}
