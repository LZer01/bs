package com.example.house.common;

import lombok.Data;

/**
 * 全局统一返回结果
 */
@Data
public class Result<T> {
    private Integer code;    // 状态码 (200:成功, 500:失败, 403:无权)
    private String msg;      // 提示消息
    private T data;          // 数据主体

    // 成功静态方法
    public static <T> Result<T> success(T data) {
        Result<T> r = new Result<>();
        r.setCode(200);
        r.setMsg("操作成功");
        r.setData(data);
        return r;
    }

    // 失败静态方法
    public static <T> Result<T> error(String msg) {
        Result<T> r = new Result<>();
        r.setCode(500);
        r.setMsg(msg);
        r.setData(null);
        return r;
    }
}