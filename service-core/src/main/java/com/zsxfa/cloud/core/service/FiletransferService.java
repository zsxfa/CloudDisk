package com.zsxfa.cloud.core.service;

import com.zsxfa.cloud.core.pojo.dto.file.DownloadFileDTO;
import com.zsxfa.cloud.core.pojo.dto.file.PreviewDTO;
import com.zsxfa.cloud.core.pojo.dto.file.UploadFileDTO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author zsxfa
 */
public interface FiletransferService {

    //存储大小
    Long selectStorageSizeByUserId(Long userId);


}
