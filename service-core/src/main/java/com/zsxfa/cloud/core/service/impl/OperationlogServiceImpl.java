package com.zsxfa.cloud.core.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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

    @Override
    public IPage<Operationlog> selectOperationLogPage(Integer current, Integer size) {
        IPage<Operationlog> page = new Page<>(current, size);
        IPage<Operationlog> list = operationlogMapper.selectPage(page, null);
        return list;
    }

    @Override
    public List<Operationlog> selectOperationLog() {
        List<Operationlog> result = operationlogMapper.selectList(null);
        return result;
    }

    @Override
    public void insertOperationLog(Operationlog operationlogBean) {
        operationlogMapper.insert(operationlogBean);

    }
}
