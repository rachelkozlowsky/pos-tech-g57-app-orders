package br.com.five.seven.food.infra.exceptions;

public class ClientNotFoundException extends RuntimeException {

    public ClientNotFoundException(String msg) {
        super(msg);
    }
}

