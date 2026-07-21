package com.he194346.mvc.online_library_management_system.service;

import com.he194346.mvc.online_library_management_system.dto.user.ChangePasswordRequestDTO;
import com.he194346.mvc.online_library_management_system.dto.user.RegisterRequestDTO;
import com.he194346.mvc.online_library_management_system.dto.user.UserResponseDTO;
import com.he194346.mvc.online_library_management_system.entity.User;

public interface UserService {

    UserResponseDTO register(RegisterRequestDTO request);

    void changePassword(String email, ChangePasswordRequestDTO request);

    User findByEmail(String email);
}
