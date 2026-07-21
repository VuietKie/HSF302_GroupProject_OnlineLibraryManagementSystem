package com.he194346.mvc.online_library_management_system.service.impl;

import com.he194346.mvc.online_library_management_system.dto.user.ChangePasswordRequestDTO;
import com.he194346.mvc.online_library_management_system.dto.user.RegisterRequestDTO;
import com.he194346.mvc.online_library_management_system.dto.user.UserResponseDTO;
import com.he194346.mvc.online_library_management_system.entity.User;
import com.he194346.mvc.online_library_management_system.enums.ErrorCode;
import com.he194346.mvc.online_library_management_system.exception.CustomException;
import com.he194346.mvc.online_library_management_system.mapper.UserMapper;
import com.he194346.mvc.online_library_management_system.repository.UserRepository;
import com.he194346.mvc.online_library_management_system.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.he194346.mvc.online_library_management_system.enums.UserRole.READER;
import static com.he194346.mvc.online_library_management_system.enums.UserStatus.ACTIVE;

@Service
@RequiredArgsConstructor
public class UserServiceIpml implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

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

    @Override
    public void changePassword(String email, ChangePasswordRequestDTO request) {
        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND, "Không tìm thấy người dùng");
        }

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.WRONG_PASSWORD, "Mật khẩu hiện tại không đúng");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    public User findByEmail(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND, "Không tìm thấy người dùng");
        }
        return user;
    }
}
