package com.test.common.entity;

import com.test.common.enums.RespCodeEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseResult<T> {

    private String code;
    private String message;
    private T data;

    public BaseResult(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public BaseResult(String code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> BaseResult<T> success(T data) {
        return new BaseResult<>(RespCodeEnum.SUCCESS.getCode(), "成功", data);
    }

    public static <T> BaseResult<T> fail(String msg) {
        return new BaseResult<>(RespCodeEnum.FAILURE.getCode(), msg);
    }

}
