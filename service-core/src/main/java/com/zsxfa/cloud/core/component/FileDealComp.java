package com.zsxfa.cloud.core.component;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zsxfa.cloud.base.util.UFOPUtils;
import com.zsxfa.cloud.core.mapper.FileMapper;
import com.zsxfa.cloud.core.mapper.UserFileMapper;
import com.zsxfa.cloud.core.pojo.entity.*;
import com.zsxfa.cloud.core.pojo.vo.FileListVo;
import com.zsxfa.cloud.core.service.*;
import com.zsxfa.common.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 文件逻辑处理组件
 */
@Slf4j
@Component
public class FileDealComp {
    @Resource
    UserFileMapper userFileMapper;
    @Resource
    UserService userService;
    @Resource
    UserFileService userFileService;

    public static Executor exec = Executors.newFixedThreadPool(10);

    /**
     * 获取重复文件名
     * @param userFile
     * @param savefilePath
     * @return
     */
    public String getRepeatFileName(UserFile userFile, String savefilePath) {
        String fileName = userFile.getFileName();
        String extendName = userFile.getExtendName();
        Integer deleteFlag = userFile.getDeleteFlag();
        Long userId = userFile.getUserId();
        int isDir = userFile.getIsDir();
        LambdaQueryWrapper<UserFile> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserFile::getFilePath, savefilePath)
                .eq(UserFile::getDeleteFlag, deleteFlag)
                .eq(UserFile::getUserId, userId)
                .eq(UserFile::getFileName, fileName)
                .eq(UserFile::getIsDir, isDir);
        if (userFile.getIsDir() == 0) {
            lambdaQueryWrapper.eq(UserFile::getExtendName, extendName);
        }
        List<UserFile> list = userFileMapper.selectList(lambdaQueryWrapper);
        if (list == null) {
            return fileName;
        }
        if (list.isEmpty()) {
            return fileName;
        }
        int i = 0;

        while (list != null && !list.isEmpty()) {
            i++;
            LambdaQueryWrapper<UserFile> lambdaQueryWrapper1 = new LambdaQueryWrapper<>();
            lambdaQueryWrapper1.eq(UserFile::getFilePath, savefilePath)
                    .eq(UserFile::getDeleteFlag, deleteFlag)
                    .eq(UserFile::getUserId, userId)
                    .eq(UserFile::getFileName, fileName + "(" + i + ")")
                    .eq(UserFile::getIsDir, isDir);
            if (userFile.getIsDir() == 0) {
                lambdaQueryWrapper1.eq(UserFile::getExtendName, extendName);
            }
            list = userFileMapper.selectList(lambdaQueryWrapper1);
        }

        return fileName + "(" + i + ")";

    }

    /**
     * 1、上传目录
     *
     * @param filePath
     * @param sessionUserId
     */
    public synchronized void restoreParentFilePath(String filePath, Long sessionUserId) {
        // 加锁，防止并发情况下有重复创建目录情况
        String parentFilePath = UFOPUtils.getParentPath(filePath);
        while(parentFilePath.contains("/")) {
            String fileName = parentFilePath.substring(parentFilePath.lastIndexOf("/") + 1);
            parentFilePath = UFOPUtils.getParentPath(parentFilePath);
            LambdaQueryWrapper<UserFile> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(UserFile::getFilePath, parentFilePath + "/" + fileName)//FileConstant.pathSeparator
                    .eq(UserFile::getFileName, fileName)
                    .eq(UserFile::getDeleteFlag, 0)
                    .eq(UserFile::getIsDir, 1)
                    .eq(UserFile::getUserId, sessionUserId);
            List<UserFile> userFileList = userFileMapper.selectList(lambdaQueryWrapper);
            System.out.println("userFileList的内容是: "+userFileList);
            System.out.println("父路径和名字是："+parentFilePath + "/" + fileName);
            if (userFileList.size() == 0) {
                UserFile userFile = new UserFile();
                userFile.setUserId(sessionUserId);
                userFile.setFileName(fileName);
                userFile.setFilePath(parentFilePath + "/" );//FileConstant.pathSeparator
                userFile.setDeleteFlag(0);
                userFile.setIsDir(1);
                userFile.setUploadTime(DateUtil.getCurrentTime());
                System.out.println("这里进入了，创建的是文件夹,同时看看FilePath是："+userFile.getFilePath());
                int insert = userFileMapper.insert(userFile);
                userFileList = userFileMapper.selectList(lambdaQueryWrapper);
                System.out.println("userFileList的内容是if判断中的: "+userFileList);
                if(insert > 0){
                    break;
                }
            }

        }
    }


    /**
     * 删除重复的子目录文件
     *
     * 当还原目录的时候，如果其子目录在文件系统中已存在，则还原之后进行去重操作
     * @param filePath
     * @param sessionUserId
     */
    public synchronized void deleteRepeatSubDirFile(String filePath, Long sessionUserId) {
        log.info("删除子目录："+filePath);
        LambdaQueryWrapper<UserFile> lambdaQueryWrapper = new LambdaQueryWrapper<>();

        LambdaQueryWrapper<UserFile> eq = lambdaQueryWrapper.eq(UserFile::getUserId, sessionUserId);
        List<UserFile> userFiles1 = userFileMapper.selectList(eq);

        lambdaQueryWrapper.select(UserFile::getFileName, UserFile::getFilePath,UserFile::getDeleteFlag)
                .likeRight(UserFile::getFilePath, filePath)
                .eq(UserFile::getIsDir, 1)
                .eq(UserFile::getDeleteFlag, 0)
                .eq(UserFile::getUserId, sessionUserId)
                .groupBy(UserFile::getFilePath, UserFile::getFileName)
                .having("count(fileName) >= 2");
        List<UserFile> repeatList = userFileMapper.selectList(lambdaQueryWrapper);
        System.out.println("这里的repeatList是："+repeatList);
        for (UserFile userFile : repeatList) {
            LambdaQueryWrapper<UserFile> lambdaQueryWrapper1 = new LambdaQueryWrapper<>();
            lambdaQueryWrapper1.eq(UserFile::getFilePath, userFile.getFilePath())
                    .eq(UserFile::getFileName, userFile.getFileName())
                    .eq(UserFile::getDeleteFlag, "0")
                    .eq(UserFile::getUserId, sessionUserId);
            List<UserFile> userFiles = userFileMapper.selectList(lambdaQueryWrapper1);
            for (int i = 0; i < userFiles.size() - 1; i ++) {
                userFileMapper.deleteById(userFiles.get(i).getUserFileId());
            }
        }
    }

    /**
     * 组织一个树目录节点，文件移动的时候使用
     * @param treeNode
     * @param id
     * @param filePath
     * @param nodeNameQueue
     * @return
     */
    public TreeNode insertTreeNode(TreeNode treeNode, long id, String filePath, Queue<String> nodeNameQueue){

        List<TreeNode> childrenTreeNodes = treeNode.getChildren();
        String currentNodeName = nodeNameQueue.peek();
        if (currentNodeName == null){
            return treeNode;
        }

        filePath = filePath + currentNodeName + "/";

        if (!isExistPath(childrenTreeNodes, currentNodeName)){  //1、判断有没有该子节点，如果没有则插入
            //插入
            TreeNode resultTreeNode = new TreeNode();

            resultTreeNode.setFilePath(filePath);
            resultTreeNode.setLabel(nodeNameQueue.poll());
            resultTreeNode.setId(++id);

            childrenTreeNodes.add(resultTreeNode);

        }else{  //2、如果有，则跳过
            nodeNameQueue.poll();
        }

        if (nodeNameQueue.size() != 0) {
            for (int i = 0; i < childrenTreeNodes.size(); i++) {

                TreeNode childrenTreeNode = childrenTreeNodes.get(i);
                if (currentNodeName.equals(childrenTreeNode.getLabel())){
                    childrenTreeNode = insertTreeNode(childrenTreeNode, id * 10, filePath, nodeNameQueue);
                    childrenTreeNodes.remove(i);
                    childrenTreeNodes.add(childrenTreeNode);
                    treeNode.setChildren(childrenTreeNodes);
                }

            }
        }else{
            treeNode.setChildren(childrenTreeNodes);
        }

        return treeNode;

    }

    /**
     * 判断该路径在树节点中是否已经存在
     * @param childrenTreeNodes
     * @param path
     * @return
     */
    public boolean isExistPath(List<TreeNode> childrenTreeNodes, String path){
        boolean isExistPath = false;

        try {
            for (int i = 0; i < childrenTreeNodes.size(); i++){
                if (path.equals(childrenTreeNodes.get(i).getLabel())){
                    isExistPath = true;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }


        return isExistPath;
    }


}
