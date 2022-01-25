package com.zsxfa.cloud.core.service.impl;

import com.zsxfa.cloud.core.pojo.entity.UploadTaskDetail;
import com.zsxfa.cloud.core.mapper.UploadTaskDetailMapper;
import com.zsxfa.cloud.core.service.UploadTaskDetailService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zsxfa
 * @since 2021-11-13
 */
@Service
public class UploadTaskDetailServiceImpl extends ServiceImpl<UploadTaskDetailMapper, UploadTaskDetail> implements UploadTaskDetailService {

    @Resource
    UploadTaskDetailMapper uploadTaskDetailMapper;

    @Override
    public List<Integer> getUploadedChunkNumList(String identifier) {
        return uploadTaskDetailMapper.selectUploadedChunkNumList(identifier);
    }
}
