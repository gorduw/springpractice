package com.testing.springpractice.exception;

public class ForbiddenException extends RuntimeException {
    private final String resourceName;
    private final String fieldValue;

    public ForbiddenException(String resourceName, String fieldValue) {
        super(String.format("Forbidden exception for %s with value %s", resourceName, fieldValue));
        this.resourceName = resourceName;
        this.fieldValue = fieldValue;
    }

    public String getResourceName() {
        return resourceName;
    }

    public String getFieldValue() {
        return fieldValue;
    }
}
