package com.colin.secondkill.util.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * 2024年05月17日17:56
 */
@AllArgsConstructor
@NoArgsConstructor
@Component
@Scope("prototype")
@Data
public class ResponseResult<T> {

    private Status status;

    private String message;

    private T data;

}
