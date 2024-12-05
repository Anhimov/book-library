package services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.anhimov.library.models.Book;
import ru.anhimov.library.models.Person;
import ru.anhimov.library.repositories.BookRepository;
import ru.anhimov.library.services.BookService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookServiceTest {
    public static final String TITLE = "Title";
    public static final String AUTHOR = "Author";
    public static final String YEAR = "year";
    public static final String OLD_TITLE = "Old Title";
    public static final String NEW_TITLE = "New Title";
    public static final String QUERY = "query";

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findAllBooksWithPaginationAndSorting() {
        Page<Book> mockPage = new PageImpl<>(Arrays.asList(new Book(), new Book()));
        when(bookRepository.findAll(PageRequest.of(0, 10, Sort.by(YEAR))))
                .thenReturn(mockPage);

        Page<Book> result = bookService.findAllBooks(0, 10, true);

        assertEquals(2, result.getContent().size());
        verify(bookRepository, times(1))
                .findAll(PageRequest.of(0, 10, Sort.by(YEAR)));
    }

    @Test
    void findAllBooksWithSorting() {
        List<Book> books = Arrays.asList(new Book(), new Book());
        when(bookRepository.findAll(Sort.by(YEAR))).thenReturn(books);

        List<Book> result = bookService.findAllBooks(true);

        assertEquals(2, result.size());
        verify(bookRepository, times(1))
                .findAll(Sort.by(YEAR));
    }

    @Test
    void findAllBooksWithUnsortedSorting() {
        List<Book> books = Arrays.asList(new Book(), new Book());
        when(bookRepository.findAll(Sort.unsorted())).thenReturn(books);

        List<Book> result = bookService.findAllBooks(false);

        assertEquals(2, result.size());
        verify(bookRepository, times(1))
                .findAll(Sort.unsorted());
    }

    @Test
    void findByIdExistingId() {
        Book book = new Book();
        when(bookRepository.findById(1))
                .thenReturn(Optional.of(book));

        Book result = bookService.findById(1);

        assertNotNull(result);
        verify(bookRepository, times(1))
                .findById(1);
    }

    @Test
    void findByIdNonExistingId() {
        when(bookRepository.findById(1))
                .thenReturn(Optional.empty());

        Book result = bookService.findById(1);

        assertNull(result);
        verify(bookRepository, times(1))
                .findById(1);
    }

    @Test
    void findBookOwnerWithOwnerPresent() {
        int bookIdWithOwner = 1;
        Person person = new Person();
        when(bookRepository.findBookOwner(bookIdWithOwner)).thenReturn(Optional.of(person));

        Optional<Person> result = bookService.findBookOwner(bookIdWithOwner);

        assertTrue(result.isPresent());
        assertEquals(person, result.get());
        verify(bookRepository, times(1)).findBookOwner(bookIdWithOwner);
    }

    @Test
    void findBookOwnerWithOwnerNotPresent() {
        int bookIdWithoutOwner = 2;
        when(bookRepository.findBookOwner(bookIdWithoutOwner)).thenReturn(Optional.empty());

        Optional<Person> result = bookService.findBookOwner(bookIdWithoutOwner);

        assertFalse(result.isPresent());
        verify(bookRepository, times(1)).findBookOwner(bookIdWithoutOwner);
    }

    @Test
    void save() {
        Book book = new Book();

        bookService.save(book);

        verify(bookRepository, times(1)).save(book);
    }

    @Test
    void updateExistingId() {
        Book existingBook = new Book();
        existingBook.setTitle(OLD_TITLE);
        Book updatedBook = new Book();
        updatedBook.setTitle(NEW_TITLE);

        when(bookRepository.findById(1)).thenReturn(Optional.of(existingBook));

        bookService.update(1, updatedBook);

        assertEquals(NEW_TITLE, existingBook.getTitle());
        verify(bookRepository, times(1)).save(existingBook);
    }

    @Test
    void updateNonExistingId() {
        Book updatedBook = new Book();
        updatedBook.setTitle(NEW_TITLE);

        when(bookRepository.findById(1)).thenReturn(Optional.empty());

        bookService.update(1, updatedBook);

        verify(bookRepository, times(1)).findById(1);
        verify(bookRepository, times(0)).save(updatedBook);
        verifyNoMoreInteractions(bookRepository);
    }

    @Test
    void assignExistingBook() {
        Book book = new Book();
        Person person = new Person();

        when(bookRepository.findById(1)).thenReturn(Optional.of(book));

        bookService.assign(1, person);

        assertEquals(person, book.getOwner());
        assertNotNull(book.getBorrowTimestamp());
        verify(bookRepository, times(1)).save(book);
    }

    @Test
    void assignNonExistingBook() {
        when(bookRepository.findById(1)).thenReturn(Optional.empty());

        bookService.assign(1, null);

        verify(bookRepository, times(1)).findById(1);
        verifyNoMoreInteractions(bookRepository);
        verify(bookRepository, times(0)).save(any());
    }

    @Test
    void releaseExistingBook() {
        Book book = new Book();
        book.setOwner(new Person());
        book.setBorrowTimestamp(LocalDateTime.now());

        when(bookRepository.findById(1)).thenReturn(Optional.of(book));

        bookService.release(1);

        assertNull(book.getOwner());
        assertNull(book.getBorrowTimestamp());
        verify(bookRepository, times(1)).save(book);
    }

    @Test
    void releaseNonExistingBook() {
        when(bookRepository.findById(1)).thenReturn(Optional.empty());

        bookService.release(1);

        verify(bookRepository, times(1)).findById(1);
        verifyNoMoreInteractions(bookRepository);
        verify(bookRepository, times(0)).save(any());
    }

    @Test
    void findBookByTitleAndAuthor() {
        Book book = new Book();
        when(bookRepository.findBookByTitleAndAuthor(TITLE, AUTHOR))
                .thenReturn(Optional.of(book));

        Optional<Book> result = bookService
                .findBookByTitleAndAuthor(TITLE, AUTHOR);

        assertTrue(result.isPresent());
        verify(bookRepository, times(1))
                .findBookByTitleAndAuthor(TITLE, AUTHOR);
    }

    @Test
    void findBookByTitleLikeIgnoreCase() {
        List<Book> books = Arrays.asList(new Book(), new Book());
        when(bookRepository.findBookByTitleLikeIgnoreCase("%" + QUERY + "%"))
                .thenReturn(books);

        List<Book> result = bookService.findBookByTitleLikeIgnoreCase(QUERY);

        assertEquals(2, result.size());
        verify(bookRepository, times(1))
                .findBookByTitleLikeIgnoreCase("%" + QUERY + "%");
    }
}

