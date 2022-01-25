package com.zsxfa.cloud.core.mapper;

import com.zsxfa.cloud.core.pojo.entity.Operationlog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author zsxfa
 */
public interface OperationlogMapper extends BaseMapper<Operationlog> {

    //获取操作日志
    List<Operationlog> userOperateList(Long page, Long limit, Long userid);
}
