package com.he194346.mvc.online_library_management_system.dto.book;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReaderBookResponseDTO {
    private Long bookId;
    private String title;
    private List<String> authorNames = new ArrayList<>();
    private List<String> categoryNames = new ArrayList<>();
    private String coverImg;
    private Integer availableCopies;
    private Integer totalCopies;
    private String description;
}
