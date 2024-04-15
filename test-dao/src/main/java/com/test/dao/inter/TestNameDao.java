package com.test.dao.inter;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.test.domain.entity.TestName;

@DS("slave")
public interface TestNameDao extends BaseMapper<TestName> {

}
