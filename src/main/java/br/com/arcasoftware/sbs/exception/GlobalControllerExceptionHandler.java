package br.com.arcasoftware.sbs.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class GlobalControllerExceptionHandler {

    Logger logger = LoggerFactory.getLogger(GlobalControllerExceptionHandler.class);

    @ExceptionHandler(ValidationException.class)
    @ResponseBody
    public ResponseEntity<CustomExceptionDTO> handleCustomSimpleException(ValidationException validationException) {
        logger.error(validationException.getDescription());
        HttpStatus responseStatus = validationException.getHttpStatus();
        return new ResponseEntity<>(new CustomExceptionDTO(validationException.getDescription()), responseStatus);
    }

}