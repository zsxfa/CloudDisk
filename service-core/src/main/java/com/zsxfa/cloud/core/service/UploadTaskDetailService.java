package com.zsxfa.cloud.core.service;

import com.zsxfa.cloud.core.pojo.entity.UploadTaskDetail;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zsxfa
 */
public interface UploadTaskDetailService extends IService<UploadTaskDetail> {

    List<Integer> getUploadedChunkNumList(String identifier);
}
