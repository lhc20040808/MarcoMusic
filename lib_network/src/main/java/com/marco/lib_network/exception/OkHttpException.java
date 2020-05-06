package com.marco.lib_network.exception;

/*
 * 自定义异常类,返回ecode,emsg到业务层
 */
public class OkHttpException extends Exception {
    private static final long serialVersionUID = 1L;

    /**
     * the server return code
     */
    private int code;

    /**
     * the server return error message
     */
    private Object msg;

    public OkHttpException(int code, Object msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public Object getMsg() {
        return msg;
    }
}