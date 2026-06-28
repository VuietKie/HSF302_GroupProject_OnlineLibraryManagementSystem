package com.he194346.mvc.online_library_management_system.dto.book;

import com.he194346.mvc.online_library_management_system.enums.BookStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookRequestDTO {
    @NotBlank(message = "Tên sách không được để trống")
    @Size(max = 100, message = "Tên sách tối đa 100 ký tự")
    private String title;

    @Size(max = 1000, message = "Mô tả tối đa 1000 ký tự")
    private String description;

    private BookStatus status;

    @NotNull(message = "Số lượng không được để trống")
    @Min(value = 0,message = "Số lượng không được âm")
    private Integer totalCopies;

    private String coverImg;

    private List<Long> authorIds= new ArrayList<>();
    private List<Long> categoryIds= new ArrayList<>();
}
