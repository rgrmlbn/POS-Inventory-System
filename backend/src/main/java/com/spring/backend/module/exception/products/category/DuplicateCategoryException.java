package com.spring.backend.module.exception.products.category;

public class DuplicateCategoryException extends RuntimeException {
    public DuplicateCategoryException() {
        super("Category already exists");
    }
}
