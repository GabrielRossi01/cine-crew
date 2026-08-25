package br.com.cinecrew.cinecrew.exception;

import org.springframework.http.HttpStatus;

public class BusinessRuleException extends ApiException {

    public BusinessRuleException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
