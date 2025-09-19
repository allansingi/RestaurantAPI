package pt.allanborges.restaurant.controller.handlers.exceptions;

public class AdminApprovalNotAllowedException extends RuntimeException {

    public AdminApprovalNotAllowedException(String message) {
        super(message);
    }

}