package com.eaglebank.feature.common.web.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@Schema
public class BadRequestErrorResponse {
    private String message;
    private List<BadRequestErrorResponseDetailsInner> details;
}
