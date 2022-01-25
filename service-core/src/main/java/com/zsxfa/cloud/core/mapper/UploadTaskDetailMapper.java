package com.zsxfa.cloud.core.mapper;

import com.zsxfa.cloud.core.pojo.entity.UploadTaskDetail;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author zsxfa
 * @since 2021-11-13
 */
public interface UploadTaskDetailMapper extends BaseMapper<UploadTaskDetail> {

    List<Integer> selectUploadedChunkNumList(String identifier);
}
