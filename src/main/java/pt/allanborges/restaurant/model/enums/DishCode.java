package pt.allanborges.restaurant.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum DishCode {

    MEAT(1L, "MEAT", "Red meat dish"),
    SEAFOOD(2L, "SEAFOOD", "Seafood dish"),
    HAMBURGER(3L, "HAMBURGER", "Hamburger dish"),
    VEGAN(4L, "VEGAN", "Vegan dish"),
    PASTA(5L, "PASTA", "Pasta dish"),
    DRINK(6L, "DRINK", "Drinks"),
    DESSERT(7L, "DESSERT", "Dessert"),
    BREAD(8L, "SIDE", "Side dish"),
    SALAD(9L, "SALAD", "Salad");

    private final Long id;
    private final String code;
    private final String description;

}