package com.zsxfa.cloud.core.mapper;

import com.zsxfa.cloud.core.pojo.entity.RecoveryFile;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zsxfa.cloud.core.pojo.vo.RecoveryFileListVo;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author zsxfa
 * @since 2021-11-13
 */
public interface RecoveryFileMapper extends BaseMapper<RecoveryFile> {
    //被删除的文件列表
    List<RecoveryFileListVo> selectRecoveryFileList(Long userId);
}
