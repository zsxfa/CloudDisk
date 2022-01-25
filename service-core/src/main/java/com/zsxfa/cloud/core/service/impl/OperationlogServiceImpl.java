package com.zsxfa.cloud.core.service.impl;

import com.zsxfa.cloud.core.mapper.UserLoginRecordMapper;
import com.zsxfa.cloud.core.pojo.entity.Operationlog;
import com.zsxfa.cloud.core.mapper.OperationlogMapper;
import com.zsxfa.cloud.core.service.OperationlogService;
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
 */
@Service
public class OperationlogServiceImpl extends ServiceImpl<OperationlogMapper, Operationlog> implements OperationlogService {

    @Resource
    OperationlogMapper operationlogMapper;

    @Override
    public List<Operationlog> userOperateList(Long page, Long limit, Long userid) {
        return operationlogMapper.userOperateList(page, limit, userid);
    }
}
