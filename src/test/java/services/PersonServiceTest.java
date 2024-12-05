package services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.anhimov.library.models.Book;
import ru.anhimov.library.models.Person;
import ru.anhimov.library.repositories.PersonRepository;
import ru.anhimov.library.services.PersonService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PersonServiceTest {
    public static final String OLD_NAME = "Old Name";
    public static final String NEW_NAME = "New Name";

    @Mock
    private PersonRepository personRepository;

    @InjectMocks
    private PersonService personService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findAllShouldReturnAllPersons() {
        List<Person> mockPersons = Arrays.asList(new Person(), new Person());
        when(personRepository.findAll()).thenReturn(mockPersons);

        List<Person> persons = personService.findAll();

        assertEquals(2, persons.size());
        verify(personRepository, times(1)).findAll();
    }

    @Test
    void findByIdShouldReturnPersonWhenPersonExists() {
        Person mockPerson = new Person();
        when(personRepository.findById(1)).thenReturn(Optional.of(mockPerson));

        Person person = personService.findById(1);

        assertNotNull(person);
        verify(personRepository, times(1)).findById(1);
    }

    @Test
    void findByIdShouldReturnNullWhenPersonDoesNotExist() {
        when(personRepository.findById(1)).thenReturn(Optional.empty());

        Person person = personService.findById(1);

        assertNull(person);
        verify(personRepository, times(1)).findById(1);
    }

    @Test
    void saveShouldCallRepositorySave() {
        Person person = new Person();
        person.setName("John Doe");
        person.setAge(30);

        personService.save(person);

        verify(personRepository, times(1)).save(eq(person));
    }

    @Test
    void updateShouldUpdateExistingPerson() {
        Person existingPerson = new Person();
        existingPerson.setName(OLD_NAME);
        existingPerson.setAge(30);

        Person updatedPerson = new Person();
        updatedPerson.setName(NEW_NAME);
        updatedPerson.setAge(35);

        when(personRepository.findById(1)).thenReturn(Optional.of(existingPerson));

        personService.update(1, updatedPerson);

        assertEquals(NEW_NAME, existingPerson.getName());
        assertEquals(35, existingPerson.getAge());
        verify(personRepository, times(1)).findById(1);
        verify(personRepository, times(1)).save(existingPerson);
    }

    @Test
    void updateShouldNotUpdateExistingPersonWhenPersonDoesNotExist() {
        when(personRepository.findById(1)).thenReturn(Optional.empty());

        personService.update(1, new Person());

        verify(personRepository, times(1)).findById(1);
        verify(personRepository, times(0)).save(any(Person.class));
        verifyNoMoreInteractions(personRepository);
    }

    @Test
    void deleteShouldCallRepositoryDeleteById() {
        personService.delete(1);

        verify(personRepository, times(1)).deleteById(1);
    }

    @Test
    void findBooksByPersonIdShouldReturnBooksWhenPersonExists() {
        Book overdueBook = new Book();
        overdueBook.setTitle("Overdue Book");
        overdueBook.setBorrowTimestamp(LocalDateTime.now().minusDays(15));

        Book notOverdueBook = new Book();
        notOverdueBook.setTitle("Not Overdue Book");
        notOverdueBook.setBorrowTimestamp(LocalDateTime.now().minusDays(5));

        Book notOverdueBookWithNullTimestamp = new Book();
        notOverdueBookWithNullTimestamp.setTitle("Not Overdue Book With Null Timestamp");
        notOverdueBookWithNullTimestamp.setBorrowTimestamp(null);

        Person mockPerson = new Person();
        mockPerson.setName("John Doe");
        mockPerson.setBooks(Arrays.asList(overdueBook, notOverdueBook, notOverdueBookWithNullTimestamp));

        when(personRepository.findById(1)).thenReturn(Optional.of(mockPerson));

        List<Book> books = personService.findBooksByPersonId(1);

        assertEquals(3, books.size());
        assertTrue(books.get(0).getIsOverdue());
        assertFalse(books.get(1).getIsOverdue());
        assertFalse(books.get(2).getIsOverdue());
        verify(personRepository, times(1)).findById(1);
    }

    @Test
    void findBooksByPersonIdShouldReturnEmptyListWhenPersonDoesNotExist() {
        when(personRepository.findById(1)).thenReturn(Optional.empty());

        List<Book> books = personService.findBooksByPersonId(1);

        assertTrue(books.isEmpty());
        verify(personRepository, times(1)).findById(1);
    }
}

