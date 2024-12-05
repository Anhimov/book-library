package ru.anhimov.library.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.anhimov.library.models.Book;
import ru.anhimov.library.models.Person;
import ru.anhimov.library.repositories.BookRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class BookService {
    private final BookRepository bookRepository;

    @Autowired
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public Page<Book> findAllBooks(Integer page, Integer size, Boolean sortByYear) {
        Sort sort = sortByYear ? Sort.by("year") : Sort.unsorted();
        return bookRepository.findAll(PageRequest.of(page, size, sort));
    }

    public List<Book> findAllBooks(Boolean sortByYear) {
        Sort sort = sortByYear ? Sort.by("year") : Sort.unsorted();
        return bookRepository.findAll(sort);
    }

    public Book findById(int id) {
        return bookRepository.findById(id).orElse(null);
    }

    public Optional<Person> findBookOwner(int id) {
        return bookRepository.findBookOwner(id);
    }

    @Transactional
    public void save(Book book) {
        bookRepository.save(book);
    }

    @Transactional
    public void update(int id, Book book) {
        bookRepository.findById(id).ifPresent(existingBook -> {
            existingBook.setTitle(book.getTitle());
            existingBook.setAuthor(book.getAuthor());
            existingBook.setYear(book.getYear());
            bookRepository.save(existingBook);
        });
    }

    @Transactional
    public void delete(int id) {
        bookRepository.deleteById(id);
    }

    @Transactional
    public void assign(int id, Person selectedPerson) {
        bookRepository.findById(id).ifPresent(book -> {
            book.setBorrowTimestamp(LocalDateTime.now());
            book.setOwner(selectedPerson);
            bookRepository.save(book);
        });
    }

    @Transactional
    public void release(int id) {
        bookRepository.findById(id).ifPresent(book -> {
            book.setBorrowTimestamp(null);
            book.setOwner(null);
            bookRepository.save(book);
        });
    }

    public Optional<Book> findBookByTitleAndAuthor(String title, String author) {
        return bookRepository.findBookByTitleAndAuthor(title, author);
    }

    public List<Book> findBookByTitleLikeIgnoreCase(String query) {
        return bookRepository.findBookByTitleLikeIgnoreCase("%" + query + "%");
    }
}
