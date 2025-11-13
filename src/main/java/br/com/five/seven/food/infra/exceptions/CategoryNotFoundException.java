package br.com.five.seven.food.infra.exceptions;

public class CategoryNotFoundException extends RuntimeException {

    public CategoryNotFoundException(String msg) {
        super(msg);
    }
}
