package br.tcc.pega.exception;

import java.util.Map;

public record ErrorResponse(
        int code,
        String errorMessage,
        Map<String, String> errorDetails
){}