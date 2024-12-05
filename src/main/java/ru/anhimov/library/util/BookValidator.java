package ru.anhimov.library.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.anhimov.library.models.Book;
import ru.anhimov.library.services.BookService;

@Component
public class BookValidator implements Validator {
    private final BookService bookService;

    @Autowired
    public BookValidator(BookService bookService) {
        this.bookService = bookService;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.equals(Book.class);
    }

    @Override
    public void validate(Object o, Errors errors) {
        Book book = (Book) o;
        if (bookService.findBookByTitleAndAuthor(book.getTitle(), book.getAuthor()).isPresent()) {
            errors.rejectValue("title", "duplicate", "Duplicate Book");
            errors.rejectValue("author", "duplicate", "Duplicate Book");
        }
    }
}
