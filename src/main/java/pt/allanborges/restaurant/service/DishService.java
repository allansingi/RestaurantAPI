package pt.allanborges.restaurant.service;

import org.springframework.data.domain.Page;
import pt.allanborges.restaurant.model.dtos.DishDTO;
import pt.allanborges.restaurant.model.dtos.DishFilterDTO;
import pt.allanborges.restaurant.model.entities.Dish;

import java.util.List;

public interface DishService {
    DishDTO createDish(final DishDTO dishDTO);
    List<DishDTO>findAllDishes();
    DishDTO getDishById(final Long dishId);
    DishDTO updateDish(final Long dishId, final DishDTO dishDTO);
    Page<DishDTO> findAllDishesPaginatedWithFilters(final Integer page,
                                           final Integer size,
                                           final String sort,
                                           final String orderBy,
                                           final DishFilterDTO filter);
}