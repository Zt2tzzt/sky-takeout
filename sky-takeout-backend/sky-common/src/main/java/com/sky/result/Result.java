package com.sky.result;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 后端统一返回结果
 * @param <T>
 */
@Schema(description = "结果对象")
@Data
public class Result<T> implements Serializable {
    @Schema(description = "编码")
    private Integer code; //编码：1成功，0和其它数字为失败
    @Schema(description = "信息")
    private String msg; //错误信息
    @Schema(description = "返回数据")
    private T data; //数据

    public static <T> Result<T> success() {
        Result<T> result = new Result<>();
        result.code = 1;
        result.msg = "操作成功";
        return result;
    }

    public static <T> Result<T> success(T object) {
        Result<T> result = new Result<>();
        result.data = object;
        result.code = 1;
        result.msg = "操作成功";
        return result;
    }

    public static Result<String> error(String msg) {
        Result<String> result = new Result<>();
        result.msg = msg;
        result.code = 0;
        return result;
    }

}
