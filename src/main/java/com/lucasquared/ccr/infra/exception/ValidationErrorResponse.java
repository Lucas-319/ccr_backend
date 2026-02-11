package com.lucasquared.ccr.infra.exception;

import java.util.List;

public record ValidationErrorResponse(String message, List<FieldErrorDTO> errors) {
}
