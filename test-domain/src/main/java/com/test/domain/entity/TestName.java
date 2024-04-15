package com.test.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.test.domain.BaseEntity;
import lombok.Getter;
import lombok.Setter;

@TableName("test_name")
@Getter
@Setter
public class TestName extends BaseEntity {

    private String name;

}
