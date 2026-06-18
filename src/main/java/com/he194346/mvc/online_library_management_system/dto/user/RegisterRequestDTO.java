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
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequestDTO {


    @Pattern(
            regexp = "^[A-Za-z\\s]+$",
            message = "Họ tên chỉ được chứa chữ cái và khoảng trắng"
    )
    @NotBlank(message = "Họ tên không được để trống")
    private String fullName;

    @Pattern(
            regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
            message = "Email không hợp lệ"
    )
    @NotBlank(message = "Email không được để trống")
    private String email;

//    @Pattern(regexp = "^0\\\\d{9}$",
//            message = "SĐT không hợp lệ")
    @NotBlank(message = "SĐT không được để trống")
    private String phone;

    @Size(min = 6, message = "Mật khẩu phải có ít nhất 6 ký tự")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).*$",
            message = "Mật khẩu phải bao gồm chữ hoa, chữ thường, số và ký tự đặc biệt"
    )
    @NotBlank(message = "Mật khẩu không được để trống")
    private String password;

}
