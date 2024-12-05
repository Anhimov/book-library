package ru.anhimov.library.services;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.anhimov.library.models.Book;
import ru.anhimov.library.models.Person;
import ru.anhimov.library.repositories.PersonRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class PersonService {
    private final PersonRepository personRepository;

    @Autowired
    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }


    public List<Person> findAll() {
        return personRepository.findAll();
    }

    public Person findById(int id) {
        return personRepository.findById(id).orElse(null);
    }

    public List<Book> findBooksByPersonId(int id) {
        return personRepository.findById(id)
                .map(person -> {
                    Hibernate.initialize(person.getBooks());
                    List<Book> books = person.getBooks();
                    books.forEach(book -> book.setIsOverdue(isBookOverdue(book)));
                    return books;
                })
                .orElse(Collections.emptyList());
    }

    @Transactional
    public void save(Person person) {
        personRepository.save(person);
    }

    @Transactional
    public void update(int id, Person person) {
        personRepository.findById(id).ifPresent(exsistingPerson -> {
            exsistingPerson.setName(person.getName());
            exsistingPerson.setAge(person.getAge());
            personRepository.save(exsistingPerson);
        });
    }

    @Transactional
    public void delete(int id) {
        personRepository.deleteById(id);
    }

    public Optional<Person> findPersonByName(String name) {
        return personRepository.findByName(name);
    }

    private boolean isBookOverdue(Book book) {
        if (book.getBorrowTimestamp() == null) {
            return false;
        }
        LocalDateTime now = LocalDateTime.now();
        return book.getBorrowTimestamp().isBefore(now.minusDays(10));
    }
}
