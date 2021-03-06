package com.zsxfa.cloud.core.controller.api;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.qiwenshare.common.exception.NotLoginException;
import com.qiwenshare.common.util.MimeUtils;
import com.zsxfa.cloud.base.util.JwtUtils;
import com.zsxfa.cloud.core.aop.MyLog;
import com.zsxfa.cloud.core.component.FileDealComp;
import com.zsxfa.cloud.core.config.fileConf.constant.UploadFileStatusEnum;
import com.zsxfa.cloud.core.config.fileConf.util.UFOPUtils;
import com.zsxfa.cloud.core.mapper.UserFileMapper;
import com.zsxfa.cloud.core.pojo.dto.file.DownloadFileDTO;
import com.zsxfa.cloud.core.pojo.dto.file.PreviewDTO;
import com.zsxfa.cloud.core.pojo.dto.file.UploadFileDTO;
import com.zsxfa.cloud.core.pojo.entity.*;
import com.zsxfa.cloud.core.pojo.vo.FileListVo;
import com.zsxfa.cloud.core.pojo.vo.UploadFileVo;
import com.zsxfa.cloud.core.service.*;
import com.zsxfa.common.exception.DefinedException;
import com.zsxfa.common.result.R;
import com.zsxfa.common.util.DateUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author zsxfa
 */
@Slf4j
@Api(tags = "文件传输管理")
@RestController
@RequestMapping("api/core/hdfs")
public class FiletransferController {

    @Resource
    UserService userService;
    @Resource
    UserFileService userFileService;
    @Resource
    FiletransferService filetransferService;
    @Resource
    StorageService storageService;
    @Resource
    HDFSService hdfsService;
    @Resource
    FileService fileService;
    @Resource
    FileDealComp fileDealComp;
    @Resource
    UploadTaskDetailService uploadTaskDetailService;
    @Resource
    UserFileMapper userFileMapper;

    public static final String CURRENT_MODULE = "文件传输接口";

    @ApiOperation("获取存储信息")
    @RequestMapping(value = "/getstorage", method = RequestMethod.GET)
    @ResponseBody
    public R getStorage(@RequestHeader("token") String token) {

        User sessionUserBean = userService.getUserByToken(token);
        if (sessionUserBean == null) {
            throw new DefinedException("未登录");
        }
        Storage storageBean = new Storage();

        storageBean.setUserId(sessionUserBean.getUserId());

        Long storageSize = filetransferService.selectStorageSizeByUserId(sessionUserBean.getUserId());
        Storage storage = new Storage();
        storage.setUserId(sessionUserBean.getUserId());
        storage.setStorageSize(storageSize);
        Long totalStorageSize = storageService.getTotalStorageSize(sessionUserBean.getUserId());
        storage.setTotalStorageSize(totalStorageSize);
        return R.ok().data("storage",storage);

    }

    @Operation(summary = "极速上传", description = "校验文件MD5判断文件是否存在，如果存在直接上传成功并返回skipUpload=true，如果不存在返回skipUpload=false需要再次调用该接口的POST方法", tags = {"filetransfer"})
    @MyLog(operation = "极速上传", module = CURRENT_MODULE)
    @RequestMapping(value = "/uploadfile", method = RequestMethod.GET)
    @ResponseBody
    public R uploadFileSpeed(UploadFileDTO uploadFileDto, HttpServletRequest request) {

//        User sessionUserBean = userService.getUserByToken(token);
        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);
        if(userId == null){
            throw new NotLoginException();
        }

        boolean isCheckSuccess = storageService.checkStorage(userId, uploadFileDto.getTotalSize());
        if (!isCheckSuccess) {
            return R.error().message("存储空间不足");
        }

        UploadFileVo uploadFileVo = new UploadFileVo();
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("identifier", uploadFileDto.getIdentifier());

        List<File> list = fileService.listByMap(param);
        if (list != null && !list.isEmpty()) {
            File file = list.get(0);
                UserFile userFile = new UserFile();
                userFile.setUserId(userId);
                String relativePath = uploadFileDto.getRelativePath();
                if (relativePath.contains("/")) {
                    userFile.setFilePath(uploadFileDto.getFilePath() + UFOPUtils.getParentPath(relativePath) + "/");
                    fileDealComp.restoreParentFilePath(uploadFileDto.getFilePath() + UFOPUtils.getParentPath(relativePath) + "/", userId);
                    fileDealComp.deleteRepeatSubDirFile(uploadFileDto.getFilePath(), userId);
                } else {
                    userFile.setFilePath(uploadFileDto.getFilePath());
                }
                String fileName = uploadFileDto.getFilename();
                userFile.setFileName(UFOPUtils.getFileNameNotExtend(fileName));
                userFile.setExtendName(UFOPUtils.getFileExtendName(fileName));
                userFile.setDeleteFlag(0);
                List<FileListVo> userFileList = userFileService.userFileList(userFile, null, null);
                if (userFileList.size() <= 0) {

                    userFile.setIsDir(0);
                    userFile.setUploadTime(DateUtil.getCurrentTime());
                    userFile.setFileId(file.getFileId());
                    //"fileName", "filePath", "extendName", "deleteFlag", "userId"

                    userFileService.save(userFile);
                    fileService.increaseFilePointCount(file.getFileId());
                }
                uploadFileVo.setSkipUpload(true);

        } else {
            uploadFileVo.setSkipUpload(false);
            List<Integer> uploaded = uploadTaskDetailService.getUploadedChunkNumList(uploadFileDto.getIdentifier());
            if (uploaded != null && !uploaded.isEmpty()) {
                uploadFileVo.setUploaded(uploaded);
            }
        }
        return R.ok().data("uploadFileVo",uploadFileVo);

    }

    @ApiOperation("上传文件")
    @MyLog(operation = "上传文件", module = CURRENT_MODULE)
    @RequestMapping(value = "/uploadfile", method = RequestMethod.POST)
    @ResponseBody
    public R uploadFile(HttpServletRequest request, UploadFileDTO uploadFileDto) {

//        User sessionUserBean = userService.getUserByToken(token);
//        if (sessionUserBean == null) {
//            throw new DefinedException("未登录");
//        }
        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);
        if(userId == null){
            throw new NotLoginException();
        }
        hdfsService.uploadFile(request, uploadFileDto, userId);
//        filetransferService.uploadFile(request, uploadFileDto, sessionUserBean.getUserId());

        //做文件夹去重操作
        QueryWrapper<UserFile> distinct_filePath = Wrappers.<UserFile>query()
                .select("distinct filePath")
                .eq("isDir", 1)
                .eq("deleteFlag", 0)
                .eq("userId", userId);

        List<UserFile> reFile = userFileMapper.selectList(distinct_filePath);


        for (UserFile userFile : reFile){
            fileDealComp.deleteRepeatSubDirFile(userFile.getFilePath(),userId);
        }

        UploadFileVo uploadFileVo = new UploadFileVo();
        return R.ok().data("uploadFileVo",uploadFileVo);

    }

    @ApiOperation("下载文件")
    @MyLog(operation = "下载文件", module = CURRENT_MODULE)
    @RequestMapping(value = "/downloadfile", method = RequestMethod.GET)
    public void downloadFile(HttpServletResponse httpServletResponse, DownloadFileDTO downloadFileDTO) {
        httpServletResponse.setContentType("application/force-download");// 设置强制下载不打开
        UserFile userFile = userFileService.getById(downloadFileDTO.getUserFileId());
        String fileName = "";
        if (userFile.getIsDir() == 1) {
            fileName = userFile.getFileName() + ".zip";
        } else {
            fileName = userFile.getFileName() + "." + userFile.getExtendName();

        }
        try {
            fileName = new String(fileName.getBytes("utf-8"), "ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        httpServletResponse.addHeader("Content-Disposition", "attachment;fileName=" + fileName);// 设置文件名

        hdfsService.downloadFile(httpServletResponse, downloadFileDTO);

    }


    @ApiOperation("预览文件")
    @GetMapping("/preview")
    public void preview(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, PreviewDTO previewDTO){
        UserFile userFile = userFileService.getById(previewDTO.getUserFileId());

        File fileBean = fileService.getById(userFile.getFileId());
        String mime= MimeUtils.getMime(userFile.getExtendName());
        httpServletResponse.setHeader("Content-Type", mime);
        String rangeString = httpServletRequest.getHeader("Range");//如果是video标签发起的请求就不会为null
        if (StringUtils.isNotEmpty(rangeString)) {
            long range = Long.valueOf(rangeString.substring(rangeString.indexOf("=") + 1, rangeString.indexOf("-")));
            httpServletResponse.setContentLength(Math.toIntExact(fileBean.getFileSize()));
            httpServletResponse.setHeader("Content-Range", String.valueOf(range + (Math.toIntExact(fileBean.getFileSize()) - 1)));
        }
        httpServletResponse.setHeader("Accept-Ranges", "bytes");

        String fileName = userFile.getFileName() + "." + userFile.getExtendName();
        try {
            fileName = new String(fileName.getBytes("utf-8"), "ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        httpServletResponse.addHeader("Content-Disposition", "fileName=" + fileName);// 设置文件名

        try {
            hdfsService.previewFile(httpServletResponse, previewDTO);
        }catch (Exception e){
            //org.apache.catalina.connector.ClientAbortException: java.io.IOException: 你的主机中的软件中止了一个已建立的连接。
            e.printStackTrace();
            log.error("该异常忽略不做处理：" + e);
        }
    }


}

