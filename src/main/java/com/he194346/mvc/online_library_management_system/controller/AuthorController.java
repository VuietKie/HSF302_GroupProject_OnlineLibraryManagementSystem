package com.he194346.mvc.online_library_management_system.controller;

import com.he194346.mvc.online_library_management_system.dto.author.AuthorRequestDTO;
import com.he194346.mvc.online_library_management_system.entity.Author;
import com.he194346.mvc.online_library_management_system.exception.CustomException;
import com.he194346.mvc.online_library_management_system.service.AuthorService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/authors")
@AllArgsConstructor
public class AuthorController {
    private final AuthorService authorService;

    //[READ] danh sách
    @GetMapping
    public String list(Model  model) {
        model.addAttribute("authors", authorService.findAll());
        return "author/list";
    }
    //[CREATE] Mở form thêm
    @GetMapping("/new")
    public String createForm(Model  model) {
        model.addAttribute("author",new AuthorRequestDTO());
        model.addAttribute("actionUrl","/admin/authors");
        model.addAttribute("formTitle","Thêm tác giả");
        return "author/form";
    }
    //[CREATE] Lưu form thêm
    @PostMapping
    public String create(@Valid @ModelAttribute("author") AuthorRequestDTO authorRequestDTO, BindingResult bindingResult, Model  model) {
        if(bindingResult.hasErrors()) {
            model.addAttribute("actionUrl","/admin/authors");
            model.addAttribute("formTitle","Thêm tác giả");
            return "author/form";
        }
        try{
            authorService.create(authorRequestDTO);
        } catch (CustomException e) {
            bindingResult.rejectValue("name","duplicate",e.getMessage());
            model.addAttribute("actionUrl","/admin/authors");
            model.addAttribute("formTitle","Thêm tác giả");
            return "author/form";
        }
        return "redirect:/admin/authors";
    }
    //[UPDATE] Mở form sửa
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id,Model model){
        Author author= authorService.findById(id);
        AuthorRequestDTO authorRequestDTO = new AuthorRequestDTO();
        authorRequestDTO.setName(author.getName());
        model.addAttribute("author",authorRequestDTO);
        model.addAttribute("actionUrl","/admin/authors/edit/"+id);
        model.addAttribute("formTitle","Sửa tác giả");
        return "author/form";
    }
    //[UPDATE] Lưu form sửa
    @PostMapping("/edit/{id}")
    public String edit(@Valid @ModelAttribute("author")AuthorRequestDTO authorRequestDTO,BindingResult bindingResult,@PathVariable Long id,Model model){
        if(bindingResult.hasErrors()) {
            model.addAttribute("actionUrl","/admin/authors/edit/"+id);
            model.addAttribute("formTitle","Sửa tác giả");
            return "author/form";
        }
        authorService.update(id,authorRequestDTO);
        return "redirect:/admin/authors";
    }
    //[DELETE] xóa
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id){
        authorService.delete(id);
        return "redirect:/admin/authors";
    }
}
