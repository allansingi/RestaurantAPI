package pt.allanborges.restaurant.model.dtos;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.Data;

import java.util.List;

@Data
public class DishFilterDTO {
    @Parameter(description = "Dish ID")
    private String id;
    @Parameter(description = "Dish name")
    private String name;
    @Parameter(description = "Dish description")
    private String description;
    @Parameter(description = "Dish price")
    private String price;
    @Parameter(description = "Dish stock")
    private String stock;
    @Parameter(description = "Dish code(s). Repeat param or comma-separate, e.g. code=DESSERT&code=MEAT or code=DESSERT,MEAT")
    private List<String> code;
    @Parameter(description = "Dish created date from")
    private String createdDateFrom;
    @Parameter(description = "Dish created date to")
    private String createdDateTo;
}