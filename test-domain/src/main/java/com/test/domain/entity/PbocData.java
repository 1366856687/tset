package com.test.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.test.domain.BaseEntity;
import lombok.Getter;
import lombok.Setter;

@TableName("pboc_data")
@Getter
@Setter
public class PbocData extends BaseEntity {

    @TableField("variable_value")
    private String variable_value;
    @TableField("variable_name")
    private String variableName;
}
