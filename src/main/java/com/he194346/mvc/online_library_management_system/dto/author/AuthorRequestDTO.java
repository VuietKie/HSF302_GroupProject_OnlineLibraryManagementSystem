package com.he194346.mvc.online_library_management_system.dto.author;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuthorRequestDTO {
    @NotBlank(message = "Tên tác giả không được để trống")
    @Size(max = 100, message = "Tên tác giả tối đa 100 ký tự")
    private String name;
}
