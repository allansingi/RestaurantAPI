package pt.allanborges.restaurant.model.mapper;

import org.mapstruct.Mapper;
import pt.allanborges.restaurant.model.dtos.DishCodeDTO;
import pt.allanborges.restaurant.model.entities.DishCode;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DishCodeMapper {
    DishCode toEntity(DishCodeDTO dishCodeDTO);
    DishCodeDTO toDTO(DishCode dishCode);
    List<DishCodeDTO> toDTOList(List<DishCode> dishCodeList);
}