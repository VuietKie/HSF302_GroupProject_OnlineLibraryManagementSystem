package com.he194346.mvc.online_library_management_system.controller;

import com.he194346.mvc.online_library_management_system.dto.category.CategoryRequestDTO;
import com.he194346.mvc.online_library_management_system.entity.Category;
import com.he194346.mvc.online_library_management_system.exception.CustomException;
import com.he194346.mvc.online_library_management_system.service.CategoryService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/categories")
@AllArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;
    //[READ] Danh sách
    @GetMapping
    public String list(Model model) {
        model.addAttribute("categories", categoryService.findAll());
        return "category/list";
    }
    //[CREATE] Mở form thêm
    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("category", new CategoryRequestDTO());
        model.addAttribute("actionUrl","/admin/categories");
        model.addAttribute("formTitle","Thêm thể loại");
        return "category/form";
    }
    //[CREATE] Lưu thêm
    @PostMapping
    public String create(@Valid @ModelAttribute("category") CategoryRequestDTO category,  BindingResult result, Model model) {
        if(result.hasErrors()){
            model.addAttribute("actionUrl","/admin/categories");
            model.addAttribute("formTitle", "Thêm thể loại");
            return "category/form";
        }
        try {
            categoryService.create(category);
        }catch (CustomException e){
            //trùng tên -> gắn lỗi vào đúng ô categoryName để hiện lên form
            result.rejectValue("categoryName","duplicate", e.getMessage());
            model.addAttribute("actionUrl","/admin/categories");
            model.addAttribute("formTitle", "Thêm thể loại");
            return "category/form";
        }
        return "redirect:/admin/categories";
    }
    //[Update] Mở form sửa
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        Category category= categoryService.findById(id);// không thấy ném CATEGORY_NOT_FOUND
        CategoryRequestDTO categoryRequestDTO = new CategoryRequestDTO();
        categoryRequestDTO.setCategoryName(category.getCategoryName());
        model.addAttribute("category", categoryRequestDTO);
        model.addAttribute("actionUrl","/admin/categories/edit/"+id);
        model.addAttribute("formTitle","Sửa thể loại");
        return "category/form";
    }
    //[Update] Lưu sửa
    @PostMapping("/edit/{id}")
    public String update(@PathVariable Long id, @Valid @ModelAttribute("category") CategoryRequestDTO category, BindingResult result, Model model) {
        if(result.hasErrors()){
            model.addAttribute("actionUrl","/admin/categories/edit/"+id);
            model.addAttribute("formTitle", "Sửa thể loại");
            return "category/form";
        }
        categoryService.update(id,category);
        return "redirect:/admin/categories";
    }

    //[DELETE] Xóa
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        categoryService.delete(id);
        return "redirect:/admin/categories";
    }
}
