package controllers;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import ru.anhimov.library.controllers.BooksController;
import ru.anhimov.library.models.Book;
import ru.anhimov.library.models.Person;
import ru.anhimov.library.services.BookService;
import ru.anhimov.library.services.PersonService;
import ru.anhimov.library.util.BookValidator;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class BooksControllerJunitTest {

    @Mock
    private BookService bookService;

    @Mock
    private PersonService personService;

    @Mock
    private BookValidator bookValidator;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private Model model;

    @InjectMocks
    private BooksController booksController;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetBooksWithPaginationAndSorting() {
        int page = 1;
        int size = 5;
        boolean sortByYear = true;
        List<Book> books = Arrays.asList(new Book(), new Book());
        Page<Book> bookPage = new PageImpl<>(books);
        when(bookService.findAllBooks(page, size, sortByYear)).thenReturn(bookPage);

        String viewName = booksController.getBooks(model, page, size, sortByYear);

        assertThat(viewName).isEqualTo("books/index");
        verify(bookService, times(1)).findAllBooks(page, size, sortByYear);
        verify(model, times(1)).addAttribute("bookPage", bookPage);
        verify(model, times(1)).addAttribute("currentPage", page);
        verify(model, times(1)).addAttribute("pageSize", size);
        verify(model, times(1)).addAttribute("totalPages", bookPage.getTotalPages());
        verify(model, times(1)).addAttribute("isSortByYear", sortByYear);
    }

    @Test
    public void testGetBooksWithoutPaginationSortingDisabled() {
        Integer page = null;
        Integer size = null;
        boolean sortByYear = false;
        List<Book> books = Arrays.asList(new Book(), new Book(), new Book());
        when(bookService.findAllBooks(sortByYear)).thenReturn(books);

        String viewName = booksController.getBooks(model, page, size, sortByYear);

        assertThat(viewName).isEqualTo("books/index");
        verify(bookService, times(1)).findAllBooks(sortByYear);
        verify(model, times(1)).addAttribute("bookPage", books);
        verify(model, times(1)).addAttribute("currentPage", 0);
        verify(model, times(1)).addAttribute("pageSize", books.size());
        verify(model, times(1)).addAttribute("totalPages", 1);
        verify(model, times(1)).addAttribute("isSortByYear", sortByYear);
    }

    @Test
    public void testGetBooksWithoutPaginationSortingEnabled() {
        Integer page = null;
        Integer size = null;
        boolean sortByYear = true;

        List<Book> books = List.of(new Book());
        when(bookService.findAllBooks(sortByYear)).thenReturn(books);

        String viewName = booksController.getBooks(model, page, size, sortByYear);

        assertThat(viewName).isEqualTo("books/index");
        verify(bookService, times(1)).findAllBooks(sortByYear);
        verify(model, times(1)).addAttribute("bookPage", books);
        verify(model, times(1)).addAttribute("currentPage", 0);
        verify(model, times(1)).addAttribute("pageSize", books.size());
        verify(model, times(1)).addAttribute("totalPages", 1);
        verify(model, times(1)).addAttribute("isSortByYear", sortByYear);
    }

    @Test
    public void testGetBookWhenOwnerIsNull() {
        int bookId = 1;
        Book book = new Book();
        book.setId(bookId);
        book.setTitle("Test Book");

        when(bookService.findById(bookId)).thenReturn(book);
        when(bookService.findBookOwner(bookId)).thenReturn(Optional.empty());

        String viewName = booksController.getBook(bookId, model, new Person());

        assertThat(viewName).isEqualTo("books/show");
        verify(model).addAttribute("book", book);
        verify(model).addAttribute("people", Collections.emptyList());
    }

    @Test
    public void testGetBookWhenOwnerIsPresent() {
        int bookId = 1;
        Book book = new Book();
        book.setId(bookId);
        book.setTitle("Test Book");

        Person owner = new Person();
        owner.setId(1);
        owner.setName("Test Owner");

        when(bookService.findById(bookId)).thenReturn(book);
        when(bookService.findBookOwner(bookId)).thenReturn(Optional.of(owner));
        when(personService.findAll()).thenReturn(Collections.emptyList());

        String viewName = booksController.getBook(bookId, model, new Person());

        assertThat(viewName).isEqualTo("books/show");
        verify(model).addAttribute("book", book);
        verify(model).addAttribute("bookOwner", owner);
    }

    @Test
    public void testAddBookWithValidationErrors() {
        Book book = new Book();
        doNothing().when(bookValidator).validate(eq(book), any(BindingResult.class));
        when(bindingResult.hasErrors()).thenReturn(true);

        String viewName = booksController.addBook(book, bindingResult);

        assertThat(viewName).isEqualTo("books/new");
        verify(bookValidator, times(1)).validate(eq(book), eq(bindingResult));
        verify(bookService, never()).save(any(Book.class));
    }

    @Test
    public void testAddBookWithNoValidationErrors() {
        Book book = new Book();
        doNothing().when(bookValidator).validate(eq(book), any(BindingResult.class));
        when(bindingResult.hasErrors()).thenReturn(false);

        String viewName = booksController.addBook(book, bindingResult);

        assertThat(viewName).isEqualTo("redirect:/books");
        verify(bookValidator, times(1)).validate(eq(book), eq(bindingResult));
        verify(bookService, times(1)).save(eq(book));
    }

    @Test
    public void testUpdateBookWithValidationErrors() {
        int bookId = 1;
        Book book = new Book();
        when(bindingResult.hasErrors()).thenReturn(true);

        String viewName = booksController.updateBook(bookId, book, bindingResult);

        assertThat(viewName).isEqualTo("books/edit");
        verify(bookService, never()).update(anyInt(), any(Book.class));
    }

    @Test
    public void testUpdateBookWithNoValidationErrors() {
        int bookId = 1;
        Book book = new Book();
        when(bindingResult.hasErrors()).thenReturn(false);

        String viewName = booksController.updateBook(bookId, book, bindingResult);

        assertThat(viewName).isEqualTo("redirect:/books");
        verify(bookService, times(1)).update(eq(bookId), eq(book));
    }

    @Test
    public void testAssignBook() {
        int bookId = 1;
        Person person = new Person();
        person.setId(42);

        String redirectUrl = booksController.assignBook(bookId, person);

        assertThat(redirectUrl).isEqualTo("redirect:/books/" + bookId);
        verify(bookService).assign(bookId, person);
    }

    @Test
    public void testReleaseBook() {
        int bookId = 1;

        String redirectUrl = booksController.releaseBook(bookId);

        assertThat(redirectUrl).isEqualTo("redirect:/books/" + bookId);
        verify(bookService).release(bookId);
    }
}

