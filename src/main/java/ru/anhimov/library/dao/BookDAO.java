package ru.anhimov.library.dao;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.anhimov.library.models.Book;
import ru.anhimov.library.models.BookMapper;
import ru.anhimov.library.models.Person;

import java.util.List;
import java.util.Optional;

public class BookDAO {
    private final JdbcTemplate jdbcTemplate;

    public BookDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Book> index() {
        return jdbcTemplate.query("SELECT * FROM book", new BookMapper());
    }

    public Book findById(int id) {
        return jdbcTemplate.query("SELECT * FROM book WHERE id=?",
                        new Object[]{id},
                        new BookMapper())
                .stream()
                .findAny()
                .orElse(null);
    }


    public Optional<Book> getBookByTitleAndAuthor(String title, String author) {
        return jdbcTemplate
                .query("SELECT * FROM book WHERE title=? AND author=?",
                        new Object[]{title, author},
                        new BookMapper())
                .stream()
                .findAny();
    }

    public void save(Book book) {
        jdbcTemplate.update("INSERT INTO book(title, author, year) VALUES (?, ?, ?)",
                book.getTitle(), book.getAuthor(), book.getYear());
    }

    public void update(int id, Book book) {
        jdbcTemplate.update("UPDATE book SET title=?, author=?, year=? WHERE id=?",
                book.getTitle(), book.getAuthor(), book.getYear(), id);
    }

    public void delete(int id) {
        jdbcTemplate.update("DELETE FROM book WHERE id=?", id);
    }

    public Optional<Person> getBookOwner(int id) {
        return jdbcTemplate.query("SELECT p.* " +
                        "FROM person p JOIN book b ON p.id = b.person_id " +
                        "WHERE b.id=?",
                new Object[]{id},
                new BeanPropertyRowMapper<>(Person.class))
                .stream()
                .findAny();
    }

    public void assign(int id, Person selectedPerson) {
        jdbcTemplate.update("UPDATE book SET person_id=? WHERE id=?", selectedPerson.getId(), id);
    }

    public void release(int id) {
        jdbcTemplate.update("UPDATE book SET person_id=NULL WHERE id=?", id);
    }
}
