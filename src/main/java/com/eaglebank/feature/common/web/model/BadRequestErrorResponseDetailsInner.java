package com.eaglebank.feature.common.web.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema
public class BadRequestErrorResponseDetailsInner {
    private String field;
    private String message;
    private String type;
}
