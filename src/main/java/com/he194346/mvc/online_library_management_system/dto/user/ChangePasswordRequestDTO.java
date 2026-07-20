package com.he194346.mvc.online_library_management_system.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordRequestDTO {

    @NotBlank(message = "Mật khẩu hiện tại không được để trống")
    private String currentPassword;

    @Size(min = 6, message = "Mật khẩu mới phải có ít nhất 6 ký tự")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).*$",
            message = "Mật khẩu mới phải bao gồm chữ hoa, chữ thường, số và ký tự đặc biệt"
    )
    @NotBlank(message = "Mật khẩu mới không được để trống")
    private String newPassword;

    @NotBlank(message = "Xác nhận mật khẩu không được để trống")
    private String confirmPassword;
}
