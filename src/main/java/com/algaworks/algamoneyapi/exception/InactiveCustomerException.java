package com.algaworks.algamoneyapi.exception;

public class InactiveCustomerException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public InactiveCustomerException(String message) {
        super(message);
    }
}
