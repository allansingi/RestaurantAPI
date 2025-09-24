package pt.allanborges.restaurant.model.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@Entity
@Table(name = "DISH_CODES")
public class DishCode extends BaseEntity implements Serializable {

    @Column(name = "CODE", length = 64, nullable = false, unique = true)
    private String code;

    @Column(name = "DESCRIPTION", length = 200)
    private String description;

}