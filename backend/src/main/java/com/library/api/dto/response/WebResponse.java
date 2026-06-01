package com.library.api.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.util.List;

/**
 * Uniform response envelope for every endpoint.
 *
 * @param <T> payload type
 *
 * @author stewicca
 * @version 1.0
 */
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record WebResponse<T>(
        int status,
        String message,
        T data,
        List<String> errors
) {
}
