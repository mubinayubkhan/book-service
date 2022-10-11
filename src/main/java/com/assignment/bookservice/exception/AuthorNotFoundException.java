package com.assignment.bookservice.exception;

public class AuthorNotFoundException extends RuntimeException {

    public AuthorNotFoundException(String errorMessage) {
        super(errorMessage);
    }
}
