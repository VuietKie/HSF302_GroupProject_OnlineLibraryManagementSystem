package com.he194346.mvc.online_library_management_system.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "categories")
@Getter
@Setter
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryId;

    @Nationalized
    private String categoryName;

    @ManyToMany(mappedBy = "categories", fetch = FetchType.LAZY)
    private Set<Book> books = new HashSet<>();
}