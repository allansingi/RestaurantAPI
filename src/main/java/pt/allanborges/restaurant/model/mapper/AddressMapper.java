package pt.allanborges.restaurant.model.mapper;

import org.mapstruct.*;
import pt.allanborges.restaurant.model.dtos.AddressDTO;
import pt.allanborges.restaurant.model.entities.Address;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AddressMapper {

    @Mapping(target = "user", ignore = true)
    Address toEntity(AddressDTO dto);

    AddressDTO toDTO(Address entity);

    List<AddressDTO> toDTOList(List<Address> list);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "updatedDate", ignore = true)
    @Mapping(target = "inactivatedBy", ignore = true)
    @Mapping(target = "inactivatedDate", ignore = true)
    @Mapping(target = "id", ignore = true)
    void updateEntityFromDTO(AddressDTO dto, @MappingTarget Address entity);

}