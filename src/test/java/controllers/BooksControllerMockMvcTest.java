package controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.anhimov.library.controllers.BooksController;
import ru.anhimov.library.models.Book;
import ru.anhimov.library.models.Person;
import ru.anhimov.library.services.BookService;
import ru.anhimov.library.services.PersonService;
import ru.anhimov.library.util.BookValidator;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringJUnitConfig(BooksControllerMockMvcTest.TestConfig.class)
class BooksControllerMockMvcTest {

    @Configuration
    static class TestConfig {
        @Bean
        public BooksController booksController(BookValidator bookValidator,
                                               BookService bookService,
                                               PersonService personService) {
            return new BooksController(bookValidator, bookService, personService);
        }

        @Bean
        public BookService bookService() {
            return Mockito.mock(BookService.class);
        }

        @Bean
        public PersonService personService() {
            return Mockito.mock(PersonService.class);
        }

        @Bean
        public BookValidator bookValidator() {
            return Mockito.mock(BookValidator.class);
        }
    }

    @Autowired
    private BookService bookService;

    @Autowired
    private PersonService personService;

    @Autowired
    private BookValidator bookValidator;

    @Autowired
    private BooksController booksController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(booksController).build();
        Mockito.reset(bookService, personService, bookValidator);
    }

    @Test
    void testGetBooksWithoutPagination() throws Exception {
        when(bookService.findAllBooks(false)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andExpect(view().name("books/index"))
                .andExpect(model().attributeExists("bookPage", "currentPage", "pageSize", "totalPages", "isSortByYear"))
                .andExpect(model().attribute("bookPage", Collections.emptyList()))
                .andExpect(model().attribute("isSortByYear", false));

        verify(bookService, times(1)).findAllBooks(false);
    }

    @Test
    void testGetBooksWithPaginationWithoutSortByYear() throws Exception {
        int page = 0;
        int size = 10;
        boolean sortByYear = false;

        Page<Book> mockPage = new PageImpl<>(Collections.emptyList(), PageRequest.of(page, size), 15);
        when(bookService.findAllBooks(page, size, sortByYear)).thenReturn(mockPage);

        mockMvc.perform(get("/books")
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(view().name("books/index"))
                .andExpect(model().attributeExists("bookPage", "currentPage", "pageSize", "totalPages", "isSortByYear"))
                .andExpect(model().attribute("bookPage", mockPage))
                .andExpect(model().attribute("currentPage", page))
                .andExpect(model().attribute("pageSize", size))
                .andExpect(model().attribute("totalPages", mockPage.getTotalPages()))
                .andExpect(model().attribute("isSortByYear", sortByYear));

        verify(bookService, times(1)).findAllBooks(page, size, sortByYear);
    }

    @Test
    void testGetBooksWithPaginationAndSortByYear() throws Exception {
        int page = 1;
        int size = 5;
        boolean sortByYear = true;

        Page<Book> mockPage = new PageImpl<>(Collections.emptyList(), PageRequest.of(page, size), 10);
        when(bookService.findAllBooks(page, size, sortByYear)).thenReturn(mockPage);

        mockMvc.perform(get("/books")
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .param("sort_by_year", String.valueOf(sortByYear)))
                .andExpect(status().isOk())
                .andExpect(view().name("books/index"))
                .andExpect(model().attributeExists("bookPage", "currentPage", "pageSize", "totalPages", "isSortByYear"))
                .andExpect(model().attribute("bookPage", mockPage))
                .andExpect(model().attribute("currentPage", page))
                .andExpect(model().attribute("pageSize", size))
                .andExpect(model().attribute("totalPages", mockPage.getTotalPages()))
                .andExpect(model().attribute("isSortByYear", sortByYear));

        verify(bookService, times(1)).findAllBooks(page, size, sortByYear);
    }

    @Test
    void testGetBookAndBookOwnerNotPresent() throws Exception {
        Book mockBook = new Book();
        mockBook.setId(1);
        mockBook.setTitle("Test Book");
        when(bookService.findById(1)).thenReturn(mockBook);
        when(bookService.findBookOwner(1)).thenReturn(Optional.empty());
        when(personService.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/books/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("books/show"))
                .andExpect(model().attributeExists("book", "people"))
                .andExpect(model().attribute("book", mockBook))
                .andExpect(model().attribute("people", Collections.emptyList()));

        verify(bookService, times(1)).findById(1);
        verify(bookService, times(1)).findBookOwner(1);
        verify(personService, times(1)).findAll();
    }

    @Test
    void testGetBookAndBookOwnerPresent() throws Exception {
        Book mockBook = new Book();
        mockBook.setId(1);
        mockBook.setTitle("Test Book");

        Person person = new Person();
        person.setBooks(List.of(mockBook));
        mockBook.setOwner(person);

        when(bookService.findById(1)).thenReturn(mockBook);
        when(bookService.findBookOwner(1)).thenReturn(Optional.of(person));

        mockMvc.perform(get("/books/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("books/show"))
                .andExpect(model().attributeExists("book", "bookOwner"))
                .andExpect(model().attribute("book", mockBook))
                .andExpect(model().attribute("bookOwner", person));

        verify(bookService, times(1)).findById(1);
        verify(bookService, times(1)).findBookOwner(1);
    }

    @Test
    void testCreateNewBookForm() throws Exception {
        mockMvc.perform(get("/books/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("books/new"))
                .andExpect(model().attributeExists("book"));
    }

    @Test
    void testAddBook() throws Exception {
        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("title", "Test Book")
                        .param("author", "Author")
                        .param("year", "2022"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books"));

        verify(bookValidator, times(1)).validate(any(), any());
        verify(bookService, times(1)).save(any());
    }

    @Test
    void testCreateEditBookForm() throws Exception {
        Book mockBook = new Book();
        mockBook.setId(1);
        mockBook.setTitle("Test Book");
        when(bookService.findById(1)).thenReturn(mockBook);

        mockMvc.perform(get("/books/1/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("books/edit"))
                .andExpect(model().attributeExists("book"))
                .andExpect(model().attribute("book", mockBook));

        verify(bookService, times(1)).findById(1);
    }

    @Test
    void testUpdateBookValidBook() throws Exception {
        Book validBook = new Book();
        validBook.setTitle("Valid Title");
        validBook.setAuthor("Valid Author");
        validBook.setYear(2022);

        mockMvc.perform(patch("/books/1")
                        .flashAttr("book", validBook))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books"));

        verify(bookService, times(1)).update(eq(1), eq(validBook));
    }

    @Test
    void testDeleteBook() throws Exception {
        mockMvc.perform(delete("/books/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books"));

        verify(bookService, times(1)).delete(1);
    }

    @Test
    void testAssignBook() throws Exception {
        Person selectedPerson = new Person();
        selectedPerson.setName("John Doe");
        selectedPerson.setAge(1990);

        mockMvc.perform(patch("/books/1/assign")
                        .flashAttr("person", selectedPerson))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books/1"));

        verify(bookService, times(1)).assign(eq(1), eq(selectedPerson));
    }

    @Test
    void testReleaseBook() throws Exception {
        mockMvc.perform(patch("/books/1/release"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books/1"));

        verify(bookService, times(1)).release(1);
    }


}

