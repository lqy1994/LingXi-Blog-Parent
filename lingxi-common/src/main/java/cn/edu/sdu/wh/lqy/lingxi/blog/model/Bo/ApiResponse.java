package cn.edu.sdu.wh.lqy.lingxi.blog.model.Bo;

import java.io.Serializable;

/**
 * rest返回对象
 */
public class ApiResponse<T> implements Serializable {

    /**
     * 服务器响应数据
     */
    private T data;

    /**
     * 请求是否成功
     */
    private boolean success;

    /**
     * 错误信息
     */
    private String msg;

    /**
     * 状态码
     */
    private int code = -1;

    /**
     * 服务器响应时间
     */
    private long timestamp;

    public ApiResponse() {
        this.timestamp = System.currentTimeMillis() / 1000;
    }

    public ApiResponse(boolean success) {
        this.timestamp = System.currentTimeMillis() / 1000;
        this.success = success;
    }

    public ApiResponse(boolean success, T data) {
        this.timestamp = System.currentTimeMillis() / 1000;
        this.success = success;
        this.data = data;
    }

    public ApiResponse(boolean success, T data, int code) {
        this.timestamp = System.currentTimeMillis() / 1000;
        this.success = success;
        this.data = data;
        this.code = code;
    }

    public ApiResponse(boolean success, String msg) {
        this.timestamp = System.currentTimeMillis() / 1000;
        this.success = success;
        this.msg = msg;
    }

    public ApiResponse(boolean success, String msg, int code) {
        this.timestamp = System.currentTimeMillis() / 1000;
        this.success = success;
        this.msg = msg;
        this.code = code;
    }

    public ApiResponse(int status, String msg) {
        this.code = status;
        this.msg = msg;
    }

    public ApiResponse(int status, String msg, T data) {
        this.code = status;
        this.msg = msg;
        this.data = data;
    }

    public static ApiResponse of(int status, String msg) {
        return new ApiResponse(status, msg);
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public static ApiResponse ok() {
        return new ApiResponse(true);
    }

    public static <T> ApiResponse ok(T data) {
        return new ApiResponse(true, data);
    }

    public static <T> ApiResponse ok(int code) {
        return new ApiResponse(true, null, code);
    }

    public static <T> ApiResponse ok(T data, int code) {
        return new ApiResponse(true, data, code);
    }

    public static ApiResponse fail() {
        return new ApiResponse(false);
    }

    public static ApiResponse fail(String msg) {
        return new ApiResponse(false, msg);
    }

    public static ApiResponse fail(int code) {
        return new ApiResponse(false, null, code);
    }

    public static ApiResponse fail(int code, String msg) {
        return new ApiResponse(false, msg, code);
    }

    public static ApiResponse ofMessage(int code, String message) {
        return new ApiResponse(code, message, null);
    }

    public static ApiResponse ofSuccess(Object data) {
        return new ApiResponse(200, "ok", data);
    }

}