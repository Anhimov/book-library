package ru.anhimov.library.controllers;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.anhimov.library.models.Book;
import ru.anhimov.library.models.Person;
import ru.anhimov.library.services.BookService;
import ru.anhimov.library.services.PersonService;
import ru.anhimov.library.util.BookValidator;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/books")
public class BooksController {
    private final BookValidator bookValidator;
    private final BookService bookService;
    private final PersonService personService;

    public BooksController(BookValidator bookValidator, BookService bookService, PersonService personService) {
        this.bookValidator = bookValidator;
        this.bookService = bookService;
        this.personService = personService;
    }

    @GetMapping
    public String getBooks(Model model,
                           @RequestParam(value = "page", required = false) Integer page,
                           @RequestParam(value = "size", required = false) Integer size,
                           @RequestParam(value = "sort_by_year", defaultValue = "false") Boolean sortByYear) {

        if (page != null && size != null) {
            Page<Book> bookPage = bookService.findAllBooks(page, size, sortByYear);
            model.addAttribute("bookPage", bookPage);
            model.addAttribute("currentPage", page);
            model.addAttribute("pageSize", size);
            model.addAttribute("totalPages", bookPage.getTotalPages());
        } else {
            List<Book> books = bookService.findAllBooks(sortByYear);
            model.addAttribute("bookPage", books);
            model.addAttribute("currentPage", 0);
            model.addAttribute("pageSize", books.size());
            model.addAttribute("totalPages", 1);
        }
        model.addAttribute("isSortByYear", sortByYear);

        return "books/index";
    }

    @GetMapping("/search")
    public String search() {
        return "books/search";
    }

    @PostMapping("/search")
    public String search(Model model,
                         @RequestParam(value = "query") String query) {

        model.addAttribute("books", bookService.findBookByTitleLikeIgnoreCase(query));
        return "books/search";
    }

    @GetMapping("/{id}")
    public String getBook(@PathVariable("id") int id,
                          Model model,
                          @ModelAttribute("person") Person person) {

        model.addAttribute("book", bookService.findById(id));
        Optional<Person> bookOwner = bookService.findBookOwner(id);

        if (bookOwner.isPresent()) {
            model.addAttribute("bookOwner", bookOwner.get());
        } else {
            model.addAttribute("people", personService.findAll());
        }
        return "books/show";
    }

    @GetMapping("/new")
    public String newBook(@ModelAttribute("book") Book book) {
        return "books/new";
    }

    @PostMapping()
    public String addBook(@ModelAttribute("book") @Valid Book book,
                          BindingResult bindingResult) {
        bookValidator.validate(book, bindingResult);

        if (bindingResult.hasErrors()) {
            return "books/new";
        }
        bookService.save(book);
        return "redirect:/books";
    }

    @GetMapping("/{id}/edit")
    public String editBook(@PathVariable("id") int id, Model model) {
        model.addAttribute("book", bookService.findById(id));
        return "books/edit";
    }

    @PatchMapping("/{id}")
    public String updateBook(@PathVariable("id") int id,
                             @ModelAttribute("book") @Valid Book book,
                             BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "books/edit";
        }
        bookService.update(id, book);
        return "redirect:/books";
    }

    @DeleteMapping("/{id}")
    public String deleteBook(@PathVariable("id") int id) {
        bookService.delete(id);
        return "redirect:/books";
    }

    @PatchMapping("/{id}/assign")
    public String assignBook(@PathVariable("id") int id, @ModelAttribute("person") Person selectedPerson) {
        bookService.assign(id, selectedPerson);
        return String.format("redirect:/books/%d", id);
    }

    @PatchMapping("/{id}/release")
    public String releaseBook(@PathVariable("id") int id) {
        bookService.release(id);
        return String.format("redirect:/books/%d", id);
    }
}
