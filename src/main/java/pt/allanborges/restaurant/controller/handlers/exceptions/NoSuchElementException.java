package pt.allanborges.restaurant.controller.handlers.exceptions;

public class NoSuchElementException extends RuntimeException {

    public NoSuchElementException(String message) {
        super(message);
    }

}