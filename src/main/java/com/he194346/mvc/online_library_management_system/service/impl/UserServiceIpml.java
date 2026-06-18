package com.he194346.mvc.online_library_management_system.service.impl;

import com.he194346.mvc.online_library_management_system.dto.user.LoginRequestDTO;
import com.he194346.mvc.online_library_management_system.dto.user.RegisterRequestDTO;
import com.he194346.mvc.online_library_management_system.dto.user.UserResponseDTO;
import com.he194346.mvc.online_library_management_system.entity.User;
import com.he194346.mvc.online_library_management_system.enums.ErrorCode;
import com.he194346.mvc.online_library_management_system.exception.CustomException;
import com.he194346.mvc.online_library_management_system.mapper.UserMapper;
import com.he194346.mvc.online_library_management_system.repository.UserRepository;
import com.he194346.mvc.online_library_management_system.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.he194346.mvc.online_library_management_system.enums.UserRole.READER;
import static com.he194346.mvc.online_library_management_system.enums.UserStatus.ACTIVE;

@Service
@AllArgsConstructor
public class UserServiceIpml implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

//    @Override
//    public UserResponseDTO login(LoginRequestDTO request) {
//        User user = userRepository.findByEmail(request.getEmail());
//        if(user == null){
//            throw new CustomException(ErrorCode.USER_NOT_FOUND, "User không tồn tại");
//        }
//        if (!user.getPassword().equals(request.getPassword())) {
//            throw new CustomException(ErrorCode.WRONG_PASSWORD, "Sai mật khẩu");
//        }
//        return userMapper.toDto(user);
//    }

    @Override
    public UserResponseDTO register(RegisterRequestDTO request) {
        if(userRepository.existsByEmail(request.getEmail())) {
            throw new CustomException(ErrorCode.USER_ALREADY_EXISTS, "Email đã tồn tại");
        }

        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        user.setRole(READER);
        user.setStatus(ACTIVE);
        return userMapper.toDto(userRepository.save(user));
    }
}
