package com.ericdriggs.reportcard.scanner;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.Map;

public class BadRequestException extends ResponseStatusException {
    private final Map<String,String> validationErrors;

    public BadRequestException(Map<String,String> validationErrors) {
        super(HttpStatus.BAD_REQUEST, buildReason(validationErrors));
        this.validationErrors = validationErrors;
    }

    public static String buildReason(Map<String,String> validationErrors) {
        return "validationErrors - " + Arrays.toString(validationErrors.entrySet().toArray());
    }

    public  Map<String,String> getValidationErrors() {
        return validationErrors;
    }


}
