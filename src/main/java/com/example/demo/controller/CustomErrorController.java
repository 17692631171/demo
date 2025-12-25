package com.example.demo.controller;

import com.example.demo.util.response.BaseResponse;
import com.example.demo.util.response.ResponseCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@RestController
@RequiredArgsConstructor
public class CustomErrorController implements ErrorController {

    private final ObjectMapper objectMapper;

    @RequestMapping("/error")
    public void handleError(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Integer statusCode = (Integer) request.getAttribute("jakarta.servlet.error.status_code");
        String requestUri = (String) request.getAttribute("jakarta.servlet.error.request_uri");

        log.warn("错误请求: {}, 状态码: {}", requestUri, statusCode);

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        if (statusCode == null) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            objectMapper.writeValue(response.getWriter(),
                    BaseResponse.fail(ResponseCode.INTERNAL_SERVER_ERROR)
            );
            return;
        }

        if (statusCode == HttpStatus.NOT_FOUND.value()) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            objectMapper.writeValue(response.getWriter(),
                    BaseResponse.fail(ResponseCode.NOT_FOUND)
            );
        } else if (statusCode == HttpStatus.FORBIDDEN.value()) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            objectMapper.writeValue(response.getWriter(),
                    BaseResponse.fail(ResponseCode.FORBIDDEN)
            );
        } else if (statusCode == HttpStatus.UNAUTHORIZED.value()) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            objectMapper.writeValue(response.getWriter(),
                    BaseResponse.fail(ResponseCode.UNAUTHORIZED)
            );
        } else if (statusCode == HttpStatus.METHOD_NOT_ALLOWED.value()) {
            response.setStatus(HttpStatus.METHOD_NOT_ALLOWED.value());
            objectMapper.writeValue(response.getWriter(),
                    BaseResponse.fail(ResponseCode.METHOD_NOT_ALLOWED)
            );
        } else if (statusCode == HttpStatus.BAD_REQUEST.value()) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            objectMapper.writeValue(response.getWriter(),
                    BaseResponse.fail(ResponseCode.BAD_REQUEST)
            );
        } else {
            response.setStatus(statusCode);
            objectMapper.writeValue(response.getWriter(),
                    BaseResponse.fail(ResponseCode.SYSTEM_ERROR)
            );
        }
    }
}