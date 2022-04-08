package com.zsxfa.cloud.core.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zsxfa.cloud.core.pojo.entity.Operationlog;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zsxfa
 * @since 2021-11-13
 */
public interface OperationlogService extends IService<Operationlog> {

    //获取操作日志
    List<Operationlog> userOperateList(Long page, Long limit, Long userid);

    IPage<Operationlog> selectOperationLogPage(Integer current, Integer size);

    List<Operationlog> selectOperationLog();

    void insertOperationLog(Operationlog operationlogBean);
}
