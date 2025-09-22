package pt.allanborges.restaurant.controller.apidocs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import pt.allanborges.restaurant.controller.handlers.exceptions.ResourceNotFoundException;
import pt.allanborges.restaurant.controller.handlers.exceptions.StandardError;
import pt.allanborges.restaurant.model.dtos.DishDTO;
import pt.allanborges.restaurant.model.dtos.DishFilterDTO;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Tag(name = "Dish Controller", description = "Responsible for manage the Dishes")
public interface DishApiDocs {

    @Operation(summary = "Create new Dish", description = "Create new Dish in order to client populate the list")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Dish created",
                    content = @Content(
                            mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = DishDTO.class)
                    )),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(
                            mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = StandardError.class)
                    )),
            @ApiResponse(
                    responseCode = "403", description = "Forbidden",
                    content = @Content(
                            mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = StandardError.class)
                    )),
            @ApiResponse(responseCode = "404", description = "Dish not found",
                    content = @Content(
                            mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = StandardError.class)
                    )),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(
                            mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = StandardError.class)
                    ))
    })
    ResponseEntity<DishDTO> createDish(@RequestBody final DishDTO dishDTO);

    @Operation(summary = "Find all dishes", description = "Find all dishes in order to client populate the list")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dish list returned",
                    content = @Content(
                            mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = DishDTO.class)
                    )),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(
                            mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = StandardError.class)
                    )),
            @ApiResponse(
                    responseCode = "403", description = "Forbidden",
                    content = @Content(
                            mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = StandardError.class)
                    )),
            @ApiResponse(responseCode = "404", description = "Request not found",
                    content = @Content(
                            mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = StandardError.class)
                    )),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(
                            mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = StandardError.class)
                    ))
    })
    ResponseEntity<List<DishDTO>> findAllDishes();

    @Operation(summary = "Find dish by id", description = "Find dish by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dish found",
                    content = @Content(
                            mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = DishDTO.class)
                    )),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(
                            mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = StandardError.class)
                    )),
            @ApiResponse(
                    responseCode = "403", description = "Forbidden",
                    content = @Content(
                            mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = StandardError.class)
                    )),
            @ApiResponse(responseCode = "404", description = "Request not found",
                    content = @Content(
                            mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ResourceNotFoundException.class)
                    )),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(
                            mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = StandardError.class)
                    ))
    })
    ResponseEntity<DishDTO> findDishById(@Parameter(description = "dishId", required = true, example = "33") final Long id);

    @Operation(summary = "Update Dish", description = "Update Dish by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dish updated",
                    content = @Content(
                            mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = DishDTO.class)
                    )),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(
                            mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = StandardError.class)
                    )),
            @ApiResponse(
                    responseCode = "403", description = "Forbidden",
                    content = @Content(
                            mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = StandardError.class)
                    )),
            @ApiResponse(responseCode = "404", description = "Request ID not found",
                    content = @Content(
                            mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ResourceNotFoundException.class)
                    )),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(
                            mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = StandardError.class)
                    ))
    })
    ResponseEntity<DishDTO> updateDish(
            @Parameter(description = "Dish id", required = true, example = "123")
            @PathVariable(name = "dishId") final Long dishId,
            @RequestBody final DishDTO dishDTO);

    @Operation(summary = "Dishes list for front office", description = "Find all dishes for front office")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "FO dishes found",
                    content = @Content(
                            mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = DishDTO.class)
                    )),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(
                            mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = StandardError.class)
                    )),
            @ApiResponse(
                    responseCode = "403", description = "Forbidden",
                    content = @Content(
                            mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = StandardError.class)
                    )),
            @ApiResponse(responseCode = "404", description = "Dishes not found",
                    content = @Content(
                            mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = StandardError.class)
                    )),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(
                            mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = StandardError.class)
                    ))
    })
    ResponseEntity<Page<DishDTO>> findAllDishesWithFilters(
            @Parameter(description = "Page number", example = "0", required = true) final Integer page,
            @Parameter(description = "Page size", example = "10", required = true) final Integer size,
            @Parameter(description = "Page sort", example = "DESC", required = true) final String sort,
            @Parameter(description = "Order by fields", example = "id") final String orderBy,
            @ModelAttribute @ParameterObject final DishFilterDTO filter
    );

}