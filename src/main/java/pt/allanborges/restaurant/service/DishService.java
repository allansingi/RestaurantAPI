package pt.allanborges.restaurant.service;

import pt.allanborges.restaurant.model.dtos.DishDTO;

import java.util.List;

public interface DishService {
    DishDTO createDish(final DishDTO dishDTO);
    List<DishDTO>findAllDishes();
    DishDTO getDishById(final Long dishId);
    DishDTO updateDish(final Long dishId, final DishDTO dishDTO);
}