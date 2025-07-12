package com.example.taskManager.common.response;


import com.example.taskManager.common.exception.Response;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.http.*;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@RestControllerAdvice
@RequiredArgsConstructor
public class ResponseWrapperAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        // Cho phép áp dụng cho mọi controller
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body,
                                  MethodParameter returnType,
                                  MediaType selectedContentType,
                                  Class selectedConverterType,
                                  ServerHttpRequest request,
                                  ServerHttpResponse response) {

        // Nếu đã là Response<T> thì không bọc nữa
        if (body instanceof Response) {
            return body;
        }

        // Nếu là lỗi (do GlobalExceptionHandler đã xử lý), không cần bọc
        if (response instanceof ServletServerHttpResponse servletResponse) {
            int status = servletResponse.getServletResponse().getStatus();
            // Nếu là lỗi (400+), không bọc nữa (vì sẽ được xử lý bởi ExceptionHandler)
            if (HttpStatus.valueOf(status).isError()) {
                return body;
            }
        }

        return Response.success(body);
    }
}
