package pt.allanborges.restaurant.model.mapper;

import org.mapstruct.*;
import pt.allanborges.restaurant.model.dtos.DishDTO;
import pt.allanborges.restaurant.model.entities.Dish;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DishMapper {

    Dish toEntity(DishDTO dishDTO);
    DishDTO toDTO(Dish dish);
    List<DishDTO> toDTOList(List<Dish> dishList);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "updatedDate", ignore = true)
    @Mapping(target = "inactivatedBy", ignore = true)
    @Mapping(target = "inactivatedDate", ignore = true)
    @Mapping(target = "id", ignore = true)
    void updateEntityFromDTO(DishDTO dto, @MappingTarget Dish entity);

}