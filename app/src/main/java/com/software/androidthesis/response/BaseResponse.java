package com.software.androidthesis.response;

/**
 * @Auther : Tcy
 * @Date : Create in 2025/1/7 21:15
 * @Decription:
 */
public class BaseResponse<T> {

    private boolean flag; //编码：true成功，false为失败
    private String msg; //错误信息
    private T data; //数据
    private int code; //状态码

    public BaseResponse() {}
    public BaseResponse(boolean flag, String msg, T data, int code) {
        this.flag = flag;
        this.msg = msg;
        this.data = data;
        this.code = code;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public boolean isSuccess() {
        return flag;
    }

    public T getMessage() {
        return (T) msg;
    }
}
