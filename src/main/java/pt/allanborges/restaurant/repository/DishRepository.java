package pt.allanborges.restaurant.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pt.allanborges.restaurant.model.dtos.DishFilterDTO;
import pt.allanborges.restaurant.model.entities.Dish;

import java.util.List;

@Repository
public interface DishRepository extends JpaRepository<Dish, Long> {
    List<Dish> findByInactivatedDateIsNull();
    Page<Dish> findByInactivatedDateIsNull(final DishFilterDTO dishFilterDTO, final Pageable pageable);
}