package com.zsxfa.cloud.core.service;

import com.zsxfa.cloud.core.pojo.dto.file.DownloadFileDTO;
import com.zsxfa.cloud.core.pojo.dto.file.PreviewDTO;
import com.zsxfa.cloud.core.pojo.dto.file.UploadFileDTO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author zsxfa
 */
public interface HDFSService {

    //上传文件
    void uploadFile(HttpServletRequest request, UploadFileDTO uploadFileDto, Long userId);

    //下载
    void downloadFile(HttpServletResponse httpServletResponse, DownloadFileDTO downloadFileDTO);

    //预览
    void previewFile(HttpServletResponse httpServletResponse, PreviewDTO previewDTO);

}
