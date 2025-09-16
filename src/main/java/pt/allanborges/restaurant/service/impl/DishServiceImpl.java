package pt.allanborges.restaurant.service.impl;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import pt.allanborges.restaurant.controller.handlers.exceptions.ResourceNotFoundException;
import pt.allanborges.restaurant.model.dtos.DishDTO;
import pt.allanborges.restaurant.model.entities.Dish;
import pt.allanborges.restaurant.model.mapper.DishMapper;
import pt.allanborges.restaurant.repository.DishRepository;
import pt.allanborges.restaurant.service.DishService;

import java.util.List;

@Log4j2
@AllArgsConstructor
@Service
public class DishServiceImpl implements DishService {

    private final DishRepository dishRepository;
    private final DishMapper dishMapper;

    @Override
    public DishDTO createDish(final DishDTO dishDTO) {
        return dishMapper.toDTO(dishRepository.save(dishMapper.toEntity(dishDTO)));
    }

    @Override
    public List<DishDTO> findAllDishes() {
        return dishMapper.toDTOList(dishRepository.findByInactivatedDateIsNull());
    }

    @Override
    public DishDTO getDishById(final Long id) {
        return dishMapper.toDTO(dishRepository.findById(id).orElseThrow(
                ()-> new ResourceNotFoundException("Dish not found. Id: " + id)));
    }

    @Override
    @Transactional
    public DishDTO updateDish(Long dishId, DishDTO dishDTO) {
        Dish current = dishRepository.findById(dishId)
                .orElseThrow(() -> new ResourceNotFoundException("Dish not found. Id: " + dishId));
        dishMapper.updateEntityFromDTO(dishDTO, current);
        Dish saved = dishRepository.save(current);
        return dishMapper.toDTO(saved);
    }

}