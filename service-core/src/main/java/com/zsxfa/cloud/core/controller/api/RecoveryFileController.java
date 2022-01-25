package com.zsxfa.cloud.core.controller.api;

import com.alibaba.fastjson.JSON;
import com.qiwenshare.common.exception.NotLoginException;
import com.zsxfa.cloud.core.pojo.dto.file.DeleteRecoveryFileDTO;
import com.zsxfa.cloud.core.pojo.dto.recoveryfile.BatchDeleteRecoveryFileDTO;
import com.zsxfa.cloud.core.pojo.dto.recoveryfile.RestoreFileDTO;
import com.zsxfa.cloud.core.pojo.entity.RecoveryFile;
import com.zsxfa.cloud.core.pojo.entity.User;
import com.zsxfa.cloud.core.pojo.entity.UserFile;
import com.zsxfa.cloud.core.pojo.vo.RecoveryFileListVo;
import com.zsxfa.cloud.core.service.RecoveryFileService;
import com.zsxfa.cloud.core.service.UserFileService;
import com.zsxfa.cloud.core.service.UserService;
import com.zsxfa.common.result.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author zsxfa
 * @since 2021-11-13
 */
@Api(tags = "文件删除管理")
@RestController
@RequestMapping("api/core/recoveryfile")
public class RecoveryFileController {

    @Resource
    RecoveryFileService recoveryFileService;
    @Resource
    UserService userService;
    @Resource
    UserFileService userFileService;

    public static final String CURRENT_MODULE = "回收站文件接口";


    @ApiOperation("回收文件列表")
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @ResponseBody
    public R getRecoveryFileList(@RequestHeader("token") String token) {
        User sessionUserBean = userService.getUserByToken(token);
        if (sessionUserBean == null) {
            throw new NotLoginException();
        }
        List<RecoveryFileListVo> recoveryFileList = recoveryFileService.selectRecoveryFileList(sessionUserBean.getUserId());
        Map<String, Object> map = new HashMap<>();
        map.put("total", recoveryFileList.size());
        map.put("recoveryFileList", recoveryFileList);
        return R.ok().data(map);
    }


    @ApiOperation("还原文件")
    @RequestMapping(value = "/restorefile", method = RequestMethod.POST)
    @ResponseBody
    public R restoreFile(@RequestBody RestoreFileDTO restoreFileDto, @RequestHeader("token") String token) {
        User sessionUserBean = userService.getUserByToken(token);
        if (sessionUserBean == null) {
            throw new NotLoginException();
        }
        recoveryFileService.restorefile(restoreFileDto.getDeleteBatchNum(), restoreFileDto.getFilePath(), sessionUserBean.getUserId());
        return R.ok().message("还原成功！");
    }

    @ApiOperation("删除回收站文件")
    @RequestMapping(value = "/deleterecoveryfile", method = RequestMethod.POST)
    @ResponseBody
    public R deleteRecoveryFile(@RequestBody DeleteRecoveryFileDTO deleteRecoveryFileDTO, @RequestHeader("token") String token) {

        User sessionUserBean = userService.getUserByToken(token);
        if (sessionUserBean == null) {
            throw new NotLoginException();
        }

        RecoveryFile recoveryFile = recoveryFileService.getById(deleteRecoveryFileDTO.getRecoveryFileId());
        UserFile userFile =userFileService.getById(recoveryFile.getUserFileId());

        recoveryFileService.deleteRecoveryFile(userFile);
        recoveryFileService.removeById(deleteRecoveryFileDTO.getRecoveryFileId());

        return R.ok().message("删除成功");
    }

    @ApiOperation("批量删除回收文件")
    @RequestMapping(value = "/batchdelete", method = RequestMethod.POST)
    @ResponseBody
    public R batchDeleteRecoveryFile(@RequestBody BatchDeleteRecoveryFileDTO batchDeleteRecoveryFileDTO, @RequestHeader("token") String token) {
        User sessionUserBean = userService.getUserByToken(token);
        if (sessionUserBean == null) {
            throw new NotLoginException();
        }
        List<RecoveryFile> recoveryFileList = JSON.parseArray(batchDeleteRecoveryFileDTO.getRecoveryFileIds(), RecoveryFile.class);
        for (RecoveryFile recoveryFile : recoveryFileList) {

            RecoveryFile recoveryFile1 = recoveryFileService.getById(recoveryFile.getRecoveryFileId());
            UserFile userFile =userFileService.getById(recoveryFile1.getUserFileId());

            recoveryFileService.deleteRecoveryFile(userFile);
            recoveryFileService.removeById(recoveryFile.getRecoveryFileId());
        }
        return R.ok().message("批量删除成功");
    }



}









