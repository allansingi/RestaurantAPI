package pt.allanborges.restaurant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pt.allanborges.restaurant.model.entities.DishCode;

import java.util.List;
import java.util.Optional;

@Repository
public interface DishCodeRepository extends JpaRepository<DishCode, Long> {
    Optional<DishCode> findByCodeIgnoreCase(final String code);
    List<DishCode> findByCodeInIgnoreCase(final List<String> codes);
}
