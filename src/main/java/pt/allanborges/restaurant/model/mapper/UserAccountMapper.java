package pt.allanborges.restaurant.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pt.allanborges.restaurant.model.entities.UserAccount;
import pt.allanborges.restaurant.security.dtos.UserResponse;

import java.util.List;

@Mapper(componentModel = "spring", uses = {AddressMapper.class})
public interface UserAccountMapper {

    @Mapping(target = "passwordHash", ignore = true)
    UserAccount toEntity(UserResponse userResponse);
    UserResponse toDTO(UserAccount userAccount);
    List<UserResponse> toDTOList(List<UserAccount> userAccountList);

}