package com.he194346.mvc.online_library_management_system.service;

import com.he194346.mvc.online_library_management_system.dto.user.ChangePasswordRequestDTO;
import com.he194346.mvc.online_library_management_system.dto.user.RegisterRequestDTO;
import com.he194346.mvc.online_library_management_system.dto.user.UserResponseDTO;

public interface UserService {

    UserResponseDTO register(RegisterRequestDTO request);

    void changePassword(String email, ChangePasswordRequestDTO request);
}
