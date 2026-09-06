package dev.fantasma.kingbank.Mapper;

import dev.fantasma.kingbank.data.models.User;
import dev.fantasma.kingbank.dtos.request.RegisterUserRequest;
import dev.fantasma.kingbank.dtos.response.UserResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User map(RegisterUserRequest request);
    UserResponse map(User user);
}
