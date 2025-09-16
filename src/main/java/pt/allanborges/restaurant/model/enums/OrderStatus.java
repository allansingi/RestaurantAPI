package pt.allanborges.restaurant.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum OrderStatus {

    PENDING(1L, "PENDING", "Order is pending payment"),
    PAID(2L, "PAID", "Order has been paid"),
    PREPARING(3L, "PREPARING", "Order is being prepared"),
    READY(4L, "READY", "Order is ready for pickup"),
    COMPLETED(5L, "COMPLETED", "Order has been completed"),
    CANCELLED(6L, "CANCELLED", "Order has been cancelled");

    private final Long id;
    private final String code;
    private final String description;
}
