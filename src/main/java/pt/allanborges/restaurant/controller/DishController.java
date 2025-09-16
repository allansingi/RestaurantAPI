package pt.allanborges.restaurant.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.allanborges.restaurant.controller.apidocs.DishApiDocs;
import pt.allanborges.restaurant.model.dtos.DishDTO;
import pt.allanborges.restaurant.service.DishService;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/v1/dishes")
public class DishController implements DishApiDocs {

    private final DishService dishService;


    @Override
    @PostMapping
    public ResponseEntity<DishDTO> createDish(@RequestBody @Valid final DishDTO dishDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(dishService.createDish(dishDTO));
    }

    @Override
    @GetMapping
    public ResponseEntity<List<DishDTO>> findAllDishes() {
        return ResponseEntity.ok().body(dishService.findAllDishes());
    }

    @Override
    @GetMapping("/{dishId}")
    public ResponseEntity<DishDTO> findDishById(@PathVariable final Long dishId) {
        return ResponseEntity.ok().body(dishService.getDishById(dishId));
    }

    @Override
    @PutMapping("/{dishId}")
    public ResponseEntity<DishDTO> updateDish(@PathVariable final Long dishId, @RequestBody @Valid final DishDTO dishDTO) {
        return ResponseEntity.ok().body(dishService.updateDish(dishId, dishDTO));
    }

}