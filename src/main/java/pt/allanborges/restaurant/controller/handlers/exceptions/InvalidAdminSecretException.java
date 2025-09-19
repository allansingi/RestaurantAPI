package pt.allanborges.restaurant.controller.handlers.exceptions;

public class InvalidAdminSecretException extends RuntimeException {

    public InvalidAdminSecretException(String message) {
        super(message);
    }

}
