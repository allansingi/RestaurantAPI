package pt.allanborges.restaurant.service;

import jakarta.annotation.Nullable;
import pt.allanborges.restaurant.model.entities.DishCode;

import java.util.List;
import java.util.Optional;

public interface DishCodeService {
    /** Find by code case-insensitively. Trims input. */
    Optional<DishCode> findByCodeIgnoreCase(String rawCode);

    /** Find many by code (case-insensitive). Trims each input; ignores blanks. */
    List<DishCode> findByCodeInIgnoreCase(List<String> rawCodes);

    /** Save (passthrough) — useful for admin screens / seeds. */
    DishCode save(DishCode dishCode);

    /** Resolve existing by code or create a new one (normalized UPPERCASE). */
    DishCode resolveOrCreate(String rawCode, @Nullable String rawDescription);
}