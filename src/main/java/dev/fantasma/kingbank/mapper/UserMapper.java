package dev.fantasma.kingbank.mapper;

import dev.fantasma.kingbank.data.models.User;
import dev.fantasma.kingbank.dtos.request.RegisterUserRequest;
import dev.fantasma.kingbank.dtos.response.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "name", expression = "java(request.getFirstName() + \" \" + request.getLastName())")
    User toEntity(RegisterUserRequest request);
    UserResponse toResponse(User user);
}
