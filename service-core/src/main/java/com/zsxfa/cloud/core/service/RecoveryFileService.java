package com.zsxfa.cloud.core.service;

import com.zsxfa.cloud.core.pojo.entity.RecoveryFile;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zsxfa.cloud.core.pojo.entity.UserFile;
import com.zsxfa.cloud.core.pojo.vo.RecoveryFileListVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zsxfa
 * @since 2021-11-13
 */
public interface RecoveryFileService extends IService<RecoveryFile> {
    //被删除的文件列表
    List<RecoveryFileListVo> selectRecoveryFileList(Long userId);

    //回复回收站中的文件
    void restorefile(String deleteBatchNum, String filePath, Long userId);

    //删除回收站的文件
    void deleteRecoveryFile(UserFile userFile);
}
