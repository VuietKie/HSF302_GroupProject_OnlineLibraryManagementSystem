package com.he194346.mvc.online_library_management_system.mapper;

import com.he194346.mvc.online_library_management_system.dto.user.LoginRequestDTO;
import com.he194346.mvc.online_library_management_system.dto.user.RegisterRequestDTO;
import com.he194346.mvc.online_library_management_system.dto.user.UserResponseDTO;
import com.he194346.mvc.online_library_management_system.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponseDTO toDto(User user);

    User toEntity(RegisterRequestDTO request);
}
