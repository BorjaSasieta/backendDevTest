package com.example.similarproducts.domain.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ExternalServiceException extends RuntimeException {
    private final String serviceName;
    private final String operation;

    public ExternalServiceException(String serviceName, String operation) {
        super("External service [%s] failed during [%s]".formatted(serviceName, operation));
        this.serviceName = serviceName;
        this.operation = operation;
    }
}