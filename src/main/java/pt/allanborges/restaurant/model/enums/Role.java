package pt.allanborges.restaurant.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Role {

    ADMIN(1L, "ADMIN", "Administrator"),
    WAITER(2L, "WAITER", "Waiter"),
    CLIENT(3L, "CLIENT", "Client"),
    KITCHEN(4L, "KITCHEN", "Kitchen");

    private final Long id;
    private final String code;
    private final String description;

}