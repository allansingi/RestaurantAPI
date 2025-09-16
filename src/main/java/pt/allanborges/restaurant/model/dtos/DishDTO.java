package pt.allanborges.restaurant.model.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DishDTO {

    private Long id;
    @NotBlank(message = "Dish name is required")
    @Size(min = 5, message = "Dish name must be at least 3 characters")
    private String name;
    @NotBlank(message = "Dish description is required")
    @Size(min = 10, message = "Dish description must be at least 5 characters")
    private String description;
    @NotNull(message = "Dish price is required")
    @Digits(integer = 10, fraction = 2, message = "Price must have up to 10 digits and 2 decimals")
    @PositiveOrZero(message = "Price must be >= 0")
    private BigDecimal price;
    @NotNull(message = "Dish stock is required")
    @PositiveOrZero(message = "Stock must be >= 0")
    private Integer stock;
    @NotBlank(message = "Dish code is required")
    @Size(min = 3, max = 64, message = "Dish code must be 3–64 characters")
    private String code;
    @NotBlank(message = "Dish image url is required")
    @Size(max = 400, message = "Image URL max 400 characters")
    private String imageUrl;
    private String createdBy;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdDate;
    private String updatedBy;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedDate;
    private String inactivatedBy;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime inactivatedDate;

}