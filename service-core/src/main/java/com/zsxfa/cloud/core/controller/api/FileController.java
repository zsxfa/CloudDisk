package com.zsxfa.cloud.core.controller.api;


import com.alibaba.csp.sentinel.util.StringUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.qiwenshare.common.exception.NotLoginException;
import com.zsxfa.cloud.base.util.JwtUtils;
import com.zsxfa.cloud.base.util.SessionUtil;
import com.zsxfa.cloud.base.util.UFOPUtils;
import com.zsxfa.cloud.core.aop.MyLog;
import com.zsxfa.cloud.core.component.FileDealComp;
import com.zsxfa.cloud.core.pojo.dto.file.*;
import com.zsxfa.cloud.core.pojo.entity.TreeNode;
import com.zsxfa.cloud.core.pojo.entity.User;
import com.zsxfa.cloud.core.pojo.entity.UserFile;
import com.zsxfa.cloud.core.pojo.query.FileQuery;
import com.zsxfa.cloud.core.pojo.vo.FileListVo;
import com.zsxfa.cloud.core.service.UserFileService;
import com.zsxfa.common.result.R;
import com.zsxfa.common.util.DateUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
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
@RequestMapping("/api/core/file")
public class FileController {

    @Resource
    UserFileService userFileService;
    @Resource
    FileDealComp fileDealComp;

    public static final String CURRENT_MODULE = "文件接口";

    @ApiOperation("获取文件列表")
    @MyLog(operation = "批量删除文件", module = CURRENT_MODULE)
    @PostMapping("/list/getfilelist")
    @ResponseBody
    public R getFileList(@RequestBody FileQuery fileQuery, HttpServletRequest request){

        String filePath = fileQuery.getFilePath();
        Long currentPage = fileQuery.getCurrentPage();
        Long pageCount = fileQuery.getPageCount();

        UserFile userFile = new UserFile();
        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);
//        User sessionUserBean = (User) SessionUtil.getSession();
        if(userId == null){
            throw new NotLoginException();
        }
        userFile.setUserId(userId);

        Long beginCount = 0L;
        if (pageCount == 0 || currentPage == 0) {
            beginCount = 0L;
            pageCount = 10L;
        } else {
            beginCount = (currentPage - 1) * pageCount;
        }

        List<FileListVo> fileList = null;
        userFile.setFilePath(UFOPUtils.urlDecode(filePath));
        if (currentPage == 0 || pageCount == 0) {
            fileList = userFileService.userFileList(userFile, beginCount, pageCount);
        } else {
            fileList = userFileService.userFileList(userFile, beginCount, pageCount);
        }

        LambdaQueryWrapper<UserFile> userFileLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userFileLambdaQueryWrapper.eq(UserFile::getUserId, userFile.getUserId())
                .eq(UserFile::getFilePath, userFile.getFilePath())
                .eq(UserFile::getDeleteFlag, 0);
        int total = userFileService.count(userFileLambdaQueryWrapper);
        System.out.println("total: "+ total);

        Map<String, Object> map = new HashMap<>();
        map.put("total", total);
        map.put("list", fileList);

        return R.ok().data(map);
    }

    @ApiOperation("通过文件类型选择文件")
    @PostMapping("/list/selectfilebyfiletype")
    @ResponseBody
    public R selectFileByFileType(@RequestBody FileQuery fileQuery, HttpServletRequest request) {
        Integer fileType = fileQuery.getFileType();
        Long currentPage = fileQuery.getCurrentPage();
        Long pageCount = fileQuery.getPageCount();

        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);
        if(userId == null){
            throw new NotLoginException();
        }
        List<FileListVo> fileList = new ArrayList<>();
        Long beginCount = 0L;
        if (pageCount == 0 || currentPage == 0) {
            beginCount = 0L;
            pageCount = 10L;
        } else {
            beginCount = (currentPage - 1) * pageCount;
        }

        Long total = 0L;
        if (fileType == UFOPUtils.OTHER_TYPE) {
            List<String> arrList = new ArrayList<>();
            arrList.addAll(Arrays.asList(UFOPUtils.DOC_FILE));
            arrList.addAll(Arrays.asList(UFOPUtils.IMG_FILE));
            arrList.addAll(Arrays.asList(UFOPUtils.VIDEO_FILE));
            arrList.addAll(Arrays.asList(UFOPUtils.MUSIC_FILE));

            fileList = userFileService.selectFileNotInExtendNames(arrList,beginCount, pageCount, userId);
            total = userFileService.selectCountNotInExtendNames(arrList,beginCount, pageCount, userId);
        } else {
            fileList = userFileService.selectFileByExtendName(UFOPUtils.getFileExtendsByType(fileType), beginCount, pageCount,userId);
            total = userFileService.selectCountByExtendName(UFOPUtils.getFileExtendsByType(fileType), beginCount, pageCount,userId);
        }
        Map<String, Object> map = new HashMap<>();
        map.put("list",fileList);
        map.put("total", total);
        return R.ok().data(map);

    }

    @ApiOperation("删除文件")
    @MyLog(operation = "删除文件", module = CURRENT_MODULE)
    @RequestMapping(value = "/deletefile", method = RequestMethod.POST)
    @ResponseBody
    public R deleteFile(@RequestBody DeleteFileDTO deleteFileDto, HttpServletRequest request) {

        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);
        if(userId == null){
            throw new NotLoginException();
        }
        userFileService.deleteUserFile(deleteFileDto.getUserFileId(), userId);

        return R.ok();
    }

    @ApiOperation("批量删除文件")
//    @MyLog(operation = "批量删除文件", module = CURRENT_MODULE)
    @RequestMapping(value = "/batchdeletefile", method = RequestMethod.POST)
    @ResponseBody
    public R deleteImageByIds(@RequestBody BatchDeleteFileDTO batchDeleteFileDto, HttpServletRequest request) {

        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);
        if(userId == null){
            throw new NotLoginException();
        }
        List<UserFile> userFiles = JSON.parseArray(batchDeleteFileDto.getFiles(), UserFile.class);
        DigestUtils.md5Hex("data");
        for (UserFile userFile : userFiles) {
            userFileService.deleteUserFile(userFile.getUserFileId(),userId);
        }

        return R.ok().message("批量删除文件成功");
    }

    @ApiOperation("创建文件夹")
    @MyLog(operation = "创建文件", module = CURRENT_MODULE)
    @RequestMapping(value = "/createfile", method = RequestMethod.POST)
    @ResponseBody
    public R createFile( @RequestBody CreateFileDTO createFileDto, HttpServletRequest request) {

        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);
        if(userId == null){
            throw new NotLoginException();
        }
        boolean isDirExist = userFileService.isDirExist(createFileDto.getFileName(), createFileDto.getFilePath(), userId);

        if (isDirExist) {
            return R.error().message("同名文件已存在");
        }
        UserFile userFile = new UserFile();
        userFile.setUserId(userId);
        userFile.setFileName(createFileDto.getFileName());
        userFile.setFilePath(createFileDto.getFilePath());
        userFile.setDeleteFlag(0);
        userFile.setIsDir(1);
        userFile.setUploadTime(DateUtil.getCurrentTime());

        userFileService.save(userFile);
        return R.ok();
    }

    @ApiOperation("文件移动")
    @MyLog(operation = "文件移动", module = CURRENT_MODULE)
    @RequestMapping(value = "/movefile", method = RequestMethod.POST)
    @ResponseBody
    public R moveFile(@RequestBody MoveFileDTO moveFileDto, HttpServletRequest request) {

        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);
        if(userId == null){
            throw new NotLoginException();
        }
        String oldfilePath = moveFileDto.getOldFilePath();
        String newfilePath = moveFileDto.getFilePath();
        String fileName = moveFileDto.getFileName();
        String extendName = moveFileDto.getExtendName();
        if (StringUtil.isEmpty(extendName)) {
            String testFilePath = oldfilePath + fileName +  "/";
            if (newfilePath.startsWith(testFilePath)) {
                return R.error().message("原路径与目标路径冲突，不能移动");
            }
        }
        userFileService.updateFilepathByFilepath(oldfilePath, newfilePath, fileName, extendName, userId);
        return R.ok();

    }

    @ApiOperation("批量移动文件")
    @MyLog(operation = "批量移动文件", module = CURRENT_MODULE)
    @RequestMapping(value = "/batchmovefile", method = RequestMethod.POST)
    @ResponseBody
    public R batchMoveFile(@RequestBody BatchMoveFileDTO batchMoveFileDto, HttpServletRequest request) {

        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);
        if(userId == null){
            throw new NotLoginException();
        }
        String files = batchMoveFileDto.getFiles();
        String newfilePath = batchMoveFileDto.getFilePath();

        List<UserFile> fileList = JSON.parseArray(files, UserFile.class);

        for (UserFile userFile : fileList) {
            if (StringUtil.isEmpty(userFile.getExtendName())) {
                String testFilePath = userFile.getFilePath() + userFile.getFileName() +  "/";
                if (newfilePath.startsWith(testFilePath)) {
                    return R.error().message("原路径与目标路径冲突，不能移动");
                }
            }
            userFileService.updateFilepathByFilepath(userFile.getFilePath(), newfilePath, userFile.getFileName(), userFile.getExtendName(), userId);
        }
        return R.ok().message("批量移动文件成功");

    }

    @ApiOperation("获取文件树,文件移动的时候需要用到该接口")
    @RequestMapping(value = "/getfiletree", method = RequestMethod.GET)
    @ResponseBody
    public R getFileTree(HttpServletRequest request) {
        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);
        if(userId == null){
            throw new NotLoginException();
        }
        List<UserFile> userFileList = userFileService.selectFilePathTreeByUserId(userId);
        TreeNode resultTreeNode = new TreeNode();
        resultTreeNode.setLabel("/");
        resultTreeNode.setId(0L);
        long id = 1;
        for (int i = 0; i < userFileList.size(); i++){
            UserFile userFile = userFileList.get(i);
            String filePath = userFile.getFilePath() + userFile.getFileName() + "/";

            Queue<String> queue = new LinkedList<>();

            String[] strArr = filePath.split("/");
            for (int j = 0; j < strArr.length; j++){
                if (!"".equals(strArr[j]) && strArr[j] != null){
                    queue.add(strArr[j]);
                }
            }
            if (queue.size() == 0){
                continue;
            }
            resultTreeNode = fileDealComp.insertTreeNode(resultTreeNode, id++, "/" , queue);
        }
        List<TreeNode> treeNodeList = resultTreeNode.getChildren();
        Collections.sort(treeNodeList, new Comparator<TreeNode>() {
            @Override
            public int compare(TreeNode o1, TreeNode o2) {
                long i = o1.getId() - o2.getId();
                return (int) i;
            }
        });
        return R.ok().data("resultTreeNode",resultTreeNode);
    }

    @ApiOperation("文件重命名")
    @MyLog(operation = "文件重命名", module = CURRENT_MODULE)
    @RequestMapping(value = "/renamefile", method = RequestMethod.POST)
    @ResponseBody
    public R renameFile(@RequestBody RenameFileDTO renameFileDto, HttpServletRequest request) {

        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);
        if(userId == null){
            throw new NotLoginException();
        }
        UserFile userFile = userFileService.getById(renameFileDto.getUserFileId());
        List<UserFile> userFiles = userFileService.selectUserFileByNameAndPath(renameFileDto.getFileName(), userFile.getFilePath(), userId);
        if (userFiles != null && !userFiles.isEmpty()) {
            return R.error().message("同名文件已存在");
        }

        LambdaUpdateWrapper<UserFile> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.set(UserFile::getFileName, renameFileDto.getFileName())
                .set(UserFile::getUploadTime, DateUtil.getCurrentTime())
                .eq(UserFile::getUserFileId, renameFileDto.getUserFileId());
        userFileService.update(lambdaUpdateWrapper);
        if (1 == userFile.getIsDir()) {
            userFileService.replaceUserFilePath(userFile.getFilePath() + renameFileDto.getFileName() + "/",
                    userFile.getFilePath() + userFile.getFileName() + "/", userId);
        }
        return R.ok();
    }

    @ApiOperation("文件搜索")
    @MyLog(operation = "文件搜索", module = CURRENT_MODULE)
    @GetMapping(value = "/search")
    @ResponseBody
    public R searchFile(SearchFileDTO searchFileDTO, HttpServletRequest request) {
        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);
        if(userId == null){
            throw new NotLoginException();
        }
        UserFile userFile = new UserFile();
        userFile.setUserId(userId);
        userFile.setFileName(searchFileDTO.getFileName());

        //设置分页
        long currentPage = (int)searchFileDTO.getCurrentPage() - 1;
        long pageCount = (int)(searchFileDTO.getPageCount() == 0 ? 10 : searchFileDTO.getPageCount());

        List<FileListVo> fileList = null;
        System.out.println("347输出userFile是："+userFile);
        fileList = userFileService.userSearchFileList(userFile, currentPage, pageCount);

        LambdaQueryWrapper<UserFile> userFileLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userFileLambdaQueryWrapper.eq(UserFile::getUserId, userFile.getUserId())
                .eq(UserFile::getDeleteFlag, 0)
                .like(UserFile::getFileName, userFile.getFileName());
        int total = userFileService.count(userFileLambdaQueryWrapper);
        System.out.println("total: "+ total);

        Map<String, Object> map = new HashMap<>();
        map.put("total", total);
        map.put("list", fileList);
        return R.ok().data("map",map);
    }

}

