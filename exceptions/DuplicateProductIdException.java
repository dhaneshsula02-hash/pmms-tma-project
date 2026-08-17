package com.acme.pmms.exceptions;

public class DuplicateProductIdException extends Exception {
    public DuplicateProductIdException(String message) {
        super(message);
    }
}