package com.test.common.exception;

import com.test.common.enums.RespCodeEnum;

public class BizException extends RuntimeException {

    /**
     * 异常码
     */
    private String code;
    /**
     * 异常描述
     */
    private String msg;

    public BizException(Throwable cause) {
        super(cause);
    }

    public BizException() {
        super();
    }

    /**
     * @param msg  错误说明，可以详细说明错误原因，方便开发定位和解决问题,返回前端时会转换为外部提示
     * @param code 错误码
     */
    public BizException(String code, String msg) {
        super(code + "-" + msg);
        this.msg = msg;
        this.code = code;
    }

    /**
     * @param code 错误码
     * @param msg  错误说明，可以详细说明错误原因，方便开发定位和解决问题,返回前端时会转换为外部提示
     *             支持使用{}作为占位符
     */
    public BizException(String code, String msg, Object... param) {
        super(BizExceptionUtils.replacePlaceholder(msg, param));
        this.msg = super.getMessage();
        this.code = code;
    }

    /**
     * 使用默认的错误码
     *
     * @param msg 错误说明，可以详细说明错误原因，方便开发定位和解决问题,返回前端时会转换为外部提示
     *            支持使用{}作为占位符
     */
    public BizException(String msg, Object[] param) {
        super(BizExceptionUtils.replacePlaceholder(msg, param));
        this.msg = super.getMessage();
        this.code = RespCodeEnum.FAILURE.getCode();
    }

    /**
     * 使用默认的错误码
     *
     * @param msg
     */
    public BizException(String msg) {
        super(RespCodeEnum.FAILURE.getCode() + "-" + msg);
        this.msg = msg;
        this.code = RespCodeEnum.FAILURE.getCode();
    }

    /**
     * 和错误码绑定
     * {@link RespCodeEnum}
     *
     * @param respCodeEnum
     */
    public BizException(RespCodeEnum respCodeEnum) {
        super(respCodeEnum.getCode() + "-" + respCodeEnum.getDesc());
        this.msg = respCodeEnum.getDesc();
        this.code = respCodeEnum.getCode();
    }

    /**
     * 此方法可以传递异常堆栈，最上层可以把打印出全部异常堆栈信息
     *
     * @param msg  错误说明，可以详细说明错误原因，方便开发定位和解决问题,返回前端时会转换为外部提示
     * @param code 错误码
     * @param t    异常
     */
    public BizException(String code, String msg, Throwable t) {
        super(t);
        this.msg = msg;
        this.code = code;
    }

    /**
     * 使用默认错误编码
     *
     * @param code
     * @param msg   支持使用{}作为占位符
     * @param t
     * @param param
     */
    public BizException(String code, Throwable t, String msg, Object... param) {
        super(t);
        this.msg = BizExceptionUtils.replacePlaceholder(msg, param);
        this.code = code;
    }


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("BizException{");
        sb.append("code='").append(code).append('\'');
        sb.append(", msg='").append(msg).append('\'');
        sb.append('}');
        return sb.toString();
    }
}