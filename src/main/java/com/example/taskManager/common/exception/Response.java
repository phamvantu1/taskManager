package com.example.taskManager.common.exception;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Response<T> {
    private String code;
    private String message;
    private T data;

    public static <T> Response<T> error(ResponseCode responseCode) {
        return new Response<>(responseCode.getCode(), responseCode.getMessage(), null);
    }

    public static <T> Response<T> success(T data) {
        return new Response<>("SUCCESS", "Success", data);
    }
}
