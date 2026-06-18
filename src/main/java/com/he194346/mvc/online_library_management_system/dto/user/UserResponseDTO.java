package com.he194346.mvc.online_library_management_system.dto.user;

import com.he194346.mvc.online_library_management_system.enums.UserRole;
import com.he194346.mvc.online_library_management_system.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {
    private String fullName;
    private String email;
    private UserRole role;
    private UserStatus status;
}
