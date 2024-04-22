package com.testing.springpractice.dto;


import lombok.Data;

@Data
public class ErrorDTO {
    private final int status;
    private final String error;
    private final String path;

    public ErrorDTO(int status, String error, String path) {
        this.status = status;
        this.error = error;
        this.path = path;
    }

}
