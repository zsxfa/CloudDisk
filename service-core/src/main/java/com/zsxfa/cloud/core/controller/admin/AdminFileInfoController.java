package com.zsxfa.cloud.core.controller.admin;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qiwenshare.common.exception.NotLoginException;
import com.zsxfa.cloud.base.util.SessionUtil;
import com.zsxfa.cloud.base.util.UFOPUtils;
import com.zsxfa.cloud.core.component.FileDealComp;
import com.zsxfa.cloud.core.mapper.AclUserRoleMapper;
import com.zsxfa.cloud.core.mapper.UserMapper;
import com.zsxfa.cloud.core.pojo.dto.file.DeleteFileDTO;
import com.zsxfa.cloud.core.pojo.dto.file.RenameFileDTO;
import com.zsxfa.cloud.core.pojo.dto.view.DisplayPage;
import com.zsxfa.cloud.core.pojo.dto.view.FileSearch;
import com.zsxfa.cloud.core.pojo.entity.*;
import com.zsxfa.cloud.core.pojo.query.FileInfoQuery;
import com.zsxfa.cloud.core.pojo.query.UserInfoQuery;
import com.zsxfa.cloud.core.pojo.vo.FileListVo;
import com.zsxfa.cloud.core.pojo.vo.RecoveryFileListVo;
import com.zsxfa.cloud.core.service.DisplayPageService;
import com.zsxfa.cloud.core.service.FileService;
import com.zsxfa.cloud.core.service.RecoveryFileService;
import com.zsxfa.cloud.core.service.UserFileService;
import com.zsxfa.common.result.R;
import com.zsxfa.common.util.DateUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author zsxfa
 */
@Api(tags = "文件管理")
@RestController
@RequestMapping("/admin/core/file")
@Slf4j
public class AdminFileInfoController {

    @Resource
    UserFileService userFileService;
    @Resource
    FileService fileService;
    @Resource
    DisplayPageService displayPageService;
    @Resource
    RecoveryFileService recoveryFileService;
    @Resource
    AclUserRoleMapper userRoleMapper;
    @Resource
    UserMapper userMapper;


    @ApiOperation("获取文件分页列表")
    @GetMapping("/list/{page}/{limit}")
    public R listPage(
            @ApiParam(value = "当前页码", required = true)
            @PathVariable Long page,
            @ApiParam(value = "每页记录数", required = true)
            @PathVariable Long limit,
            @ApiParam(value = "username", required = false)
            @PathVariable String  username) {
        //判断是否是管理员
        Long isAdmin = judgeRole(username);
        List<FileListVo> fileList = null;
        if(isAdmin != 0){
            fileList = userFileService.adminFileList(page, limit, null);
            for(FileListVo listVo : fileList){
                User user = userFileService.getUserInfo(listVo.getUserId(), listVo.getFileId());
                listVo.setUserName(user.getUsername());
            }
        }else{
            fileList = userFileService.adminFileList(page, limit, isAdmin);
            for(FileListVo listVo : fileList){
                listVo.setUserName(username);
            }
        }

        LambdaQueryWrapper<UserFile> userFileLambdaQueryWrapper = new LambdaQueryWrapper<>();
        if(isAdmin == 0){
            userFileLambdaQueryWrapper.eq(UserFile::getUserId, isAdmin);
        }
        int total = userFileService.count(userFileLambdaQueryWrapper);

        Map<String, Object> map = new HashMap<>();
        map.put("total", total);
        map.put("list", fileList);

        return R.ok().data(map);
    }

    @ApiOperation("通过文件类型选择文件")
    @GetMapping("/listByType/{page}/{limit}/{username}")
    public R selectFileByFileType(
            @ApiParam(value = "当前页码", required = true)
            @PathVariable Long page,
            @ApiParam(value = "每页记录数", required = true)
            @PathVariable Long limit,
            @ApiParam(value = "文件名", required = false)
            @RequestParam(value="fileName",required=false) String fileName,
            @ApiParam(value = "文件类型", required = false)
            @RequestParam(value="fileType",required=false) Integer fileType,
            @ApiParam(value = "username", required = false)
            @PathVariable String  username) {

        Long isAdmin = judgeRole(username);

        List<FileListVo> searchUserFileList = new ArrayList<>();
        Long count = 0L;

        if(fileType == null){
            if(isAdmin == null){//表示是admin
                Page<UserFile> userFilePage = userFileService.searchFileList(page, limit, fileName, null);
                List<UserFile> records = userFilePage.getRecords();
                for (int i = 0; i < records.size(); i++) {
                    Long userId = records.get(i).getUserId();
                    Long fileId = records.get(i).getFileId();
                    User user = userFileService.getUserInfo(userId, fileId);
                    File file = fileService.getFileInfo(fileId);

                    FileListVo listVo = new FileListVo();
                    listVo.setFileId(records.get(i).getUserFileId());
                    listVo.setFileName(records.get(i).getFileName());
                    listVo.setFileSize(file.getFileSize());
                    listVo.setExtendName(records.get(i).getExtendName());
                    listVo.setUploadTime(records.get(i).getUploadTime());
                    listVo.setFilePath(records.get(i).getFilePath());
                    listVo.setUserName(user.getUsername());
                    searchUserFileList.add(listVo);
                }
            }else{
                Page<UserFile> userFilePage = userFileService.searchFileList(page, limit, fileName, isAdmin);
                List<UserFile> records = userFilePage.getRecords();
                for (int i = 0; i < records.size(); i++) {
                    Long fileId = records.get(i).getFileId();
                    File file = fileService.getFileInfo(fileId);

                    FileListVo listVo = new FileListVo();
                    listVo.setFileId(records.get(i).getUserFileId());
                    listVo.setFileName(records.get(i).getFileName());
                    listVo.setFileSize(file.getFileSize());
                    listVo.setExtendName(records.get(i).getExtendName());
                    listVo.setUploadTime(records.get(i).getUploadTime());
                    listVo.setFilePath(records.get(i).getFilePath());
                    listVo.setUserName(username);
                    searchUserFileList.add(listVo);
                }
            }
            LambdaQueryWrapper<UserFile> userFileLambdaQueryWrapper = new LambdaQueryWrapper<>();
            userFileLambdaQueryWrapper.eq(UserFile::getIsDir,0);
            if(isAdmin != null){//表示是普通用户
                userFileLambdaQueryWrapper.eq(UserFile::getUserId, isAdmin);
            }
            count = (long)userFileService.count(userFileLambdaQueryWrapper);
        }else{
//            if (limit == 0 || page == 0) {
//                page = 0L;
//                limit = 10L;
//            } else {
//                page = (page - 1) * limit;
//            }
            if (fileType == UFOPUtils.OTHER_TYPE) {
                List<String> arrList = new ArrayList<>();
                arrList.addAll(Arrays.asList(UFOPUtils.DOC_FILE));
                arrList.addAll(Arrays.asList(UFOPUtils.IMG_FILE));
                arrList.addAll(Arrays.asList(UFOPUtils.VIDEO_FILE));
                arrList.addAll(Arrays.asList(UFOPUtils.MUSIC_FILE));

                if(isAdmin == null){
                    searchUserFileList = userFileService.adminsSelectFileNotInExtendNames(arrList,page, limit, null);
                    count = userFileService.adminSelectCountNotInExtendNames(arrList,page, limit, null);
                }else{
                    searchUserFileList = userFileService.adminsSelectFileNotInExtendNames(arrList,page, limit, isAdmin);
                    count = userFileService.adminSelectCountNotInExtendNames(arrList,page, limit, isAdmin);
                }
            } else {
                if(isAdmin == null) {//admin用户
                    searchUserFileList = userFileService.adminSelectFileByExtendName(UFOPUtils.getFileExtendsByType(fileType), page, limit, null);
                    count = userFileService.adminSelectCountByExtendName(UFOPUtils.getFileExtendsByType(fileType), page, limit, null);
                }else{
                    searchUserFileList = userFileService.adminSelectFileByExtendName(UFOPUtils.getFileExtendsByType(fileType), page, limit, isAdmin);
                    count = userFileService.adminSelectCountByExtendName(UFOPUtils.getFileExtendsByType(fileType), page, limit, isAdmin);
                }
            }

            for(FileListVo listVo : searchUserFileList){
                User user = userFileService.getUserInfo(listVo.getUserId(), listVo.getFileId());
                listVo.setUserName(user.getUsername());
            }
        }

        Map<String, Object> map = new HashMap<>();
        map.put("list",searchUserFileList);
        map.put("total", count);
        return R.ok().data(map);
    }

    @ApiOperation("修改文件名")
    @GetMapping("/update/{userFileId}/{userFileName}")
    public R selectFileByFileType(@ApiParam(value = "文件id", required = true)
                                      @PathVariable Long userFileId,
                                  @ApiParam(value = "要修改的文件名", required = true)
                                      @PathVariable String userFileName){

        UserFile userFile = userFileService.getById(userFileId);
        List<UserFile> userFiles = userFileService.selectUserFileByNameAndPath(userFileName, userFile.getFilePath(), userFile.getUserId());
        if (userFiles != null && !userFiles.isEmpty()) {
            return R.error().message("同名文件已存在");
        }

        LambdaUpdateWrapper<UserFile> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.set(UserFile::getFileName, userFileName)
                .set(UserFile::getUploadTime, DateUtil.getCurrentTime())
                .eq(UserFile::getUserFileId, userFileId);
        userFileService.update(lambdaUpdateWrapper);
        if (1 == userFile.getIsDir()) {
            userFileService.replaceUserFilePath(userFile.getFilePath() + userFileName + "/",
                    userFile.getFilePath() + userFile.getFileName() + "/", userFile.getUserId());
        }

        return R.ok();
    }

    @ApiOperation("删除文件")
    @GetMapping("/remove/{userFileId}")
    public R deleteFile(@ApiParam(value = "文件id", required = true)
                            @PathVariable Long userFileId) {
        UserFile userFile = userFileService.getById(userFileId);
        //删除到回收站
        userFileService.deleteUserFile(userFileId, userFile.getUserId());
        //删除回收站
        List<RecoveryFileListVo> recoveryFileList = recoveryFileService.selectRecoveryFileList(userFile.getUserId());
        for(RecoveryFileListVo reFile : recoveryFileList){
            RecoveryFile recoveryFile = recoveryFileService.getById(reFile.getRecoveryFileId());
            UserFile userFile2 =userFileService.getById(recoveryFile.getUserFileId());

            recoveryFileService.deleteRecoveryFile(userFile2);
            recoveryFileService.removeById(reFile.getRecoveryFileId());
        }
        return R.ok();
    }


    @ApiOperation("文件图表展示")
    @GetMapping("/show/{username}")
    public R showFile(@ApiParam(value = "username", required = false)
            @PathVariable String  username){
        Long isAdmin = judgeRole(username);
        DisplayPage displayPage = new DisplayPage();
        Long totalData;
        //如果是管理员
        if(isAdmin == null){
            totalData = displayPageService.search(null);
        }else{
            totalData = displayPageService.search(isAdmin);
        }
        displayPage.setAllCount(totalData);

        Long count = 0L;
        for (int i = 1; i < 6; i++) {
            if (i == UFOPUtils.OTHER_TYPE) {
                List<String> arrList = new ArrayList<>();
                arrList.addAll(Arrays.asList(UFOPUtils.DOC_FILE));
                arrList.addAll(Arrays.asList(UFOPUtils.IMG_FILE));
                arrList.addAll(Arrays.asList(UFOPUtils.VIDEO_FILE));
                arrList.addAll(Arrays.asList(UFOPUtils.MUSIC_FILE));
                if(isAdmin == null){
                    count = userFileService.adminSelectCountNotInExtendNames(arrList,null, null, null);
                }else{
                    count = userFileService.adminSelectCountNotInExtendNames(arrList,null, null, isAdmin);
                }
            } else {
                if(isAdmin == null){
                    count = userFileService.adminSelectCountByExtendName(UFOPUtils.getFileExtendsByType(i), null, null, null);
                }else{
                    count = userFileService.adminSelectCountByExtendName(UFOPUtils.getFileExtendsByType(i), null, null, isAdmin);
                }
            }
            if(i == 1) {
                displayPage.setPictureCount(count);
            }else if(i == 2){
                displayPage.setDocCount(count);
            }else if(i == 3){
                displayPage.setVideoCount(count);
            }else if(i == 4){
                displayPage.setMusicCount(count);
            }else{
                displayPage.setOtherCount(count);
            }
        }

        String[] arr = new String[7];
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = null;
        for (int i = 0; i < 7; i++) {
            c = Calendar.getInstance();
            c.add(Calendar.DAY_OF_MONTH, -i);
            arr[6 - i] = sdf.format(c.getTime());
        }
        displayPage.setDate(arr);

        FileSearch fileSearch = null;
        String[] abc = new String[5];

        for (int i = 1; i < 6; i++) {
            if (i == UFOPUtils.OTHER_TYPE) {
                List<String> arrList = new ArrayList<>();
                arrList.addAll(Arrays.asList(UFOPUtils.DOC_FILE));
                arrList.addAll(Arrays.asList(UFOPUtils.IMG_FILE));
                arrList.addAll(Arrays.asList(UFOPUtils.VIDEO_FILE));
                arrList.addAll(Arrays.asList(UFOPUtils.MUSIC_FILE));
                if(isAdmin == null){
                    fileSearch = displayPageService.adminDisplaySelectCountNotInExtendNames(arrList, null);
                }else{
                    fileSearch = displayPageService.adminDisplaySelectCountNotInExtendNames(arrList, isAdmin);
                }
            } else {
                if(isAdmin == null){
                    fileSearch = displayPageService.adminDisplaySelectCountByExtendName(UFOPUtils.getFileExtendsByType(i), null);
                }else{
                    fileSearch = displayPageService.adminDisplaySelectCountByExtendName(UFOPUtils.getFileExtendsByType(i), isAdmin);
                }
            }

            System.out.println("fileSearch是：" + fileSearch);
            abc[i - 1] = fileSearch.toSting();

        }
        displayPage.setAbc(abc);
        return R.ok().data("displayPage",displayPage);
    }


    //返回的是用户的id，如果是admin，就返回null
    public Long judgeRole(String username){
        //通过name查询user
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username",username);
        User user = userMapper.selectOne(wrapper);
        //通过id查权限
        QueryWrapper<AclUserRole> wrapper2 = new QueryWrapper<>();
        wrapper2.eq("user_id", user.getUserId());
        wrapper2.eq("role_id", 1);
        //判断是否具有admin权限
        Integer con = userRoleMapper.selectCount(wrapper2);
        System.out.println("count 的值是："+ con);
        return con != 0 ? null : user.getUserId();
    }
}

