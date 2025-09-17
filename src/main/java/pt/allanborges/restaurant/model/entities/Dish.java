package pt.allanborges.restaurant.model.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import pt.allanborges.restaurant.model.enums.DishCode;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "DISHES")
public class Dish extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Column(name = "NAME", length = 200, nullable = false)
    private String name;

    @Column(name = "DESCRIPTION", length = 1000)
    private String description;

    @Column(name = "PRICE", precision = 12, scale = 2, nullable = false)
    @Digits(integer = 10, fraction = 2)
    private BigDecimal price;

    @Column(name = "STOCK", nullable = false)
    private Integer stock;

    @Enumerated(EnumType.STRING)
    @Column(name = "CODE", length = 64)
    private DishCode code;

    @Column(name = "IMAGE_URL", length = 400)
    private String imageUrl;

}