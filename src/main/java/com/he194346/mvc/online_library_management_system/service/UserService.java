package com.he194346.mvc.online_library_management_system.service;

import com.he194346.mvc.online_library_management_system.dto.user.LoginRequestDTO;
import com.he194346.mvc.online_library_management_system.dto.user.RegisterRequestDTO;
import com.he194346.mvc.online_library_management_system.dto.user.UserResponseDTO;
import com.he194346.mvc.online_library_management_system.entity.User;

public interface UserService {

//    UserResponseDTO login(LoginRequestDTO request);

    UserResponseDTO register(RegisterRequestDTO request);
}
