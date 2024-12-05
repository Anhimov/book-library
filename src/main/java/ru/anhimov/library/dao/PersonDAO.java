package ru.anhimov.library.dao;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.anhimov.library.models.Book;
import ru.anhimov.library.models.BookMapper;
import ru.anhimov.library.models.Person;

import java.util.List;
import java.util.Optional;

public class PersonDAO {

    private final JdbcTemplate jdbcTemplate;

    public PersonDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Person> index() {
        return jdbcTemplate.query("SELECT * FROM Person", new BeanPropertyRowMapper<>(Person.class));
    }

    public Person findById(int id) {
        return jdbcTemplate.query("SELECT * FROM Person WHERE id=?",
                        new Object[]{id},
                        new BeanPropertyRowMapper<>(Person.class))
                .stream().findAny().orElse(null);
    }

    public void save(Person person) {
        jdbcTemplate.update("INSERT INTO person(name, age) VALUES(?, ?)", person.getName(), person.getAge());
    }

    public void update(int id, Person updatedPerson) {
        jdbcTemplate.update("UPDATE Person SET name=?, age=? WHERE id=?", updatedPerson.getName(),
                updatedPerson.getAge(), id);
    }

    public void delete(int id) {
        jdbcTemplate.update("DELETE FROM Person WHERE id=?", id);
    }

    public Optional<Person> getPersonByName(String name) {
        return jdbcTemplate
                .query("SELECT * FROM person WHERE name=?",
                        new Object[]{name},
                        new BeanPropertyRowMapper<>(Person.class))
                .stream()
                .findAny();
    }

    public List<Book> getBooksByPersonId(int id) {
        return jdbcTemplate.query("SELECT * FROM book WHERE person_id=?",
                new Object[]{id},
                new BookMapper());
    }
}
