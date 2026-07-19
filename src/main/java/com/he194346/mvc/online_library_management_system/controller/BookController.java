package com.he194346.mvc.online_library_management_system.controller;

import com.he194346.mvc.online_library_management_system.dto.book.BookRequestDTO;
import com.he194346.mvc.online_library_management_system.entity.Author;
import com.he194346.mvc.online_library_management_system.entity.Book;
import com.he194346.mvc.online_library_management_system.enums.BookStatus;
import com.he194346.mvc.online_library_management_system.service.AuthorService;
import com.he194346.mvc.online_library_management_system.service.BookService;
import com.he194346.mvc.online_library_management_system.service.CategoryService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Controller
@RequestMapping("/admin/books")
@AllArgsConstructor
public class BookController {
    private final BookService bookService;
    private final AuthorService authorService;
    private final CategoryService categoryService;

    //[READ] Danh sách
    @GetMapping
    public String list(Model model){
        model.addAttribute("books", bookService.findAll());
        return "book/list";
    }
    //[CREATE] Mở form thêm
    @GetMapping("/new")
    public String createForm(Model model){
        BookRequestDTO dto = new BookRequestDTO();
        dto.setStatus(BookStatus.ACTIVE);
        model.addAttribute("book", dto);
        model.addAttribute("actionUrl","/admin/books");
        model.addAttribute("formTitle","Thêm sách");
        loadOptions(model);
        return "book/form";
    }

    //[CREATE] Lưu thêm
    @PostMapping
    public String create(@Valid @ModelAttribute("book") BookRequestDTO book, BindingResult result, @RequestParam("coverFile")MultipartFile coverFile, Model model){
        if(result.hasErrors()){
            model.addAttribute("actionUrl", "/admin/books");
            model.addAttribute("formTitle","Thêm sách");
            loadOptions(model);
            return "book/form";
        }
        book.setCoverImg(storeFile(coverFile));
        bookService.create(book);
        return "redirect:/admin/books";
    }

    //[UPDATE] Mở form sửa
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model){
        Book b= bookService.findById(id);
        BookRequestDTO dto = new BookRequestDTO();
        dto.setTitle(b.getTitle());
        dto.setDescription(b.getDescription());
        dto.setStatus(b.getStatus());
        dto.setTotalCopies(b.getTotalCopies());
        dto.setCoverImg(b.getCoverImg());
        dto.setAuthorIds(b.getAuthors().stream().map(Author::getAuthorId).toList());
        dto.setCategoryIds(b.getCategories().stream().map(c -> c.getCategoryId()).toList());
        model.addAttribute("book", dto);
        model.addAttribute("actionUrl","/admin/books/edit/"+id);
        model.addAttribute("formTitle","Sửa sách");
        loadOptions(model);
        return "book/form";
    }
    //[UPDATE] Lưu sửa
    @PostMapping("/edit/{id}")
    public String edit(@PathVariable Long id, @Valid @ModelAttribute("book") BookRequestDTO  book, BindingResult result, @RequestParam("coverFile")MultipartFile coverFile, Model model){
        if(result.hasErrors()){
            model.addAttribute("actionUrl", "/admin/books/edit/"+id);
            model.addAttribute("formTitle","Sửa sách");
            loadOptions(model);
            return "book/form";
        }
        //chỉ thay ảnh khi người dùng chọn file mới; bỏ trống-> giữ nguyên ảnh cũ
        if (!coverFile.isEmpty()){
            book.setCoverImg(storeFile(coverFile));
        }
        bookService.update(id, book);
        return "redirect:/admin/books";
    }
    //[DELETE] Xóa
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id){
        bookService.delete(id);
        return "redirect:/admin/books";
    }

    // UC10: Approve New Books - Phê duyệt sách mới
    @GetMapping("/pending")
    public String pendingBooks(Model model) {
        model.addAttribute("pendingBooks", bookService.getPendingBooks());
        return "book/pending";
    }

    @PostMapping("/approve/{id}")
    public String approveBook(@PathVariable Long id) {
        bookService.approveBook(id);
        return "redirect:/admin/books/pending";
    }

    @PostMapping("/reject/{id}")
    public String rejectBook(@PathVariable Long id) {
        bookService.rejectBook(id);
        return "redirect:/admin/books/pending";
    }

    //đổ sẵn danh sách tác giả + thể loại cho ô chọn nhiều
    private void loadOptions(Model model) {
        model.addAttribute("allAuthors", authorService.findAll());
        model.addAttribute("allCategories", categoryService.findAll());
    }

    //lưu file ảnh vào thư mục uploads/, trả về đường dẫn /uploads/<tên>
    private String storeFile(MultipartFile coverFile) {
        if (coverFile==null || coverFile.isEmpty()) {
            return null;
        }
        try {
            Path dir= Paths.get("uploads");
            Files.createDirectories(dir);
            String fileName= System.currentTimeMillis()+"_"+coverFile.getOriginalFilename();
            Files.copy(coverFile.getInputStream(), dir.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
            return "/uploads/" + fileName;
        }catch (IOException e){
            throw new RuntimeException("Không lưu được ảnh: "+e.getMessage(),e);
        }
    }
}
