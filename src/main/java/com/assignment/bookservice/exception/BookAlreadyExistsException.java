package com.assignment.bookservice.exception;

public class BookAlreadyExistsException extends RuntimeException {

    public BookAlreadyExistsException(String errorMessage) {
        super(errorMessage);
    }
}
