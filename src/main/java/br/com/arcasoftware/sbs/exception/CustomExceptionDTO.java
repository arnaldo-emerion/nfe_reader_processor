package br.com.arcasoftware.sbs.exception;

import lombok.Getter;

@Getter
public class CustomExceptionDTO {
    private final String message;
    public CustomExceptionDTO(String message) {
        this.message = message;
    }

}
