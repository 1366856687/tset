package com.test.common.enums;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * @Describe： facade接口的统一返回码
 *
 * 注意：MEL，megrez_loan简写，无其他实际意义
 *
 * @Author: liuke
 * @Date: 2018/6/11 16:07
 */
@Getter
public enum RespCodeEnum {
    SUCCESS("00000", "成功"),
    FAILURE("55555", "失败"),
    NEEDTRY("88888", "重试"),
    UNKNOWN("99999", "未知"),
    PARAM_CHECK_ERROR("11111", "参数校验不通过"), // 参数校验不通过都可以抛出该异常
    APPLY_REPEATED("22222", "申请号重复"), //幂等校验不通过时抛出
    BIZ_CHECK_ERROR("33333", "业务检查不通过"), // 业务检查不通过
    BIZ_ERROR_PROMPT("44444", "业务检查不通过"), // 业务检查不通过,用户能够看懂的业务提示,该异常码的描述注意文案,有可能会给普通用户进行展示

    SYS_WARN("WARN55555", "系统异常告警"),

    /**数据库相关错误：MEL0101开头，后面3位用来存对应的错误码*/
    DB_EXCEPTION("MEL0101001", "数据库异常"),
    DB_QUERY_EXCEPTION("MEL0101002", "数据库查询异常"),
    DB_INSERT_EXCEPTION("MEL0101003", "数据库插入异常"),
    DB_UPDATE_EXCEPTION("MEL0101004", "数据库更新异常"),
    DB_DELETE_EXCEPTION("MEL0101005", "数据库删除异常")

    ;

    private String code;
    private String desc;
    private static Map<String, RespCodeEnum> codeMap = new HashMap();
    private static Map<String, RespCodeEnum> descMap = new HashMap();

    private RespCodeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "RespCodeEnum." + this.name() + "{code=" + this.getCode() + ", desc=" + this.getDesc() + "}";
    }

    /**
     * 按code反查枚举项
     * @param code
     * @return
     */
    public static RespCodeEnum getEnumByCode(String code){
        for (RespCodeEnum respCodeEnum : RespCodeEnum.values()){
            if (respCodeEnum.getCode().equals(code)){
                return respCodeEnum;
            }
        }
        return null;
    }

    static {
        RespCodeEnum[] arr = values();
        int len = arr.length;

        for(int i = 0; i < len; ++i) {
            RespCodeEnum type = arr[i];
            codeMap.put(type.getCode(), type);
            descMap.put(type.getDesc(), type);
        }

    }
}