package pt.allanborges.restaurant.service.impl;

import jakarta.annotation.Nullable;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import pt.allanborges.restaurant.model.entities.DishCode;
import pt.allanborges.restaurant.repository.DishCodeRepository;
import pt.allanborges.restaurant.service.DishCodeService;

import java.util.List;
import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
public class DishCodeServiceImpl implements DishCodeService {

    private final DishCodeRepository dishCodeRepository;

    @Override
    public Optional<DishCode> findByCodeIgnoreCase(String rawCode) {
        return dishCodeRepository.findByCodeIgnoreCase(normalize(rawCode));
    }

    @Override
    public List<DishCode> findByCodeInIgnoreCase(List<String> rawCodes) {
        var normalized = rawCodes == null ? List.<String>of()
                : rawCodes.stream()
                .filter(s -> s != null && !s.isBlank())
                .map(this::normalize)
                .toList();
        return normalized.isEmpty() ? List.of() : dishCodeRepository.findByCodeInIgnoreCase(normalized);
    }

    @Override
    @Transactional
    public DishCode save(DishCode dishCode) {
        // minimal normalization guard
        dishCode.setCode(normalize(dishCode.getCode()));
        if (dishCode.getDescription() != null) {
            dishCode.setDescription(dishCode.getDescription().trim());
        }
        return dishCodeRepository.save(dishCode);
    }

    @Override
    @Transactional
    public DishCode resolveOrCreate(String rawCode, @Nullable String rawDescription) {
        String normalized = normalize(rawCode);
        return dishCodeRepository.findByCodeIgnoreCase(normalized)
                .orElseGet(() -> dishCodeRepository.save(
                        DishCode.builder()
                                .code(normalized)
                                .description(normalizeDesc(rawDescription))
                                .build()
                ));
    }

    /* ---------- helpers ---------- */

    private String normalize(String code) {
        if (code == null || code.isBlank())
            throw new IllegalArgumentException("Dish code is required");
        String normalized = code.trim().toUpperCase();
        if (normalized.length() < 3 || normalized.length() > 64)
            throw new IllegalArgumentException("Dish code must be between 3 and 64 characters");
        return normalized;
    }

    private String normalizeDesc(String desc) {
        return (desc == null || desc.isBlank()) ? null : desc.trim();
    }

}