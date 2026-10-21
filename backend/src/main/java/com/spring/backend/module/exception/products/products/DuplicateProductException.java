package com.spring.backend.module.exception.products.products;

public class DuplicateProductException extends RuntimeException {
    public DuplicateProductException() {
        super("Product already exists");
    }
}
