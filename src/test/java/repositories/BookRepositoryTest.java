package repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import ru.anhimov.library.config.SpringDataSourceTestConfig;
import ru.anhimov.library.models.Book;
import ru.anhimov.library.models.Person;
import ru.anhimov.library.repositories.BookRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {SpringDataSourceTestConfig.class})
@TestPropertySource("classpath:hibernate-test.properties")
@ActiveProfiles("test")
@Transactional
class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @PersistenceContext
    private EntityManager em;

    @BeforeEach
    void setUp() {
        Person person = new Person();
        person.setName("John Doe");
        em.persist(person);

        Book book1 = new Book();
        book1.setTitle("Spring in Action");
        book1.setAuthor("Craig Walls");
        book1.setOwner(person);
        em.persist(book1);

        Book book2 = new Book();
        book2.setTitle("Effective Java: Programming Language Guide");
        book2.setAuthor("Joshua Bloch");
        em.persist(book2);

        Book book3 = new Book();
        book3.setTitle("Java Concurrency in Practice");
        book3.setAuthor("Joshua Bloch");
        em.persist(book3);

        em.flush();
    }

    @Test
    void testFindBookOwner() {
        Optional<Person> owner = bookRepository.findBookOwner(1);
        assertThat(owner).isPresent();
        assertThat(owner.get().getName()).isEqualTo("John Doe");
    }

    @Test
    void testFindBookByTitleLikeIgnoreCase() {
        List<Book> books = bookRepository.findBookByTitleLikeIgnoreCase("%spring%");
        assertThat(books).hasSize(1);
        assertThat(books.get(0).getTitle()).isEqualTo("Spring in Action");
    }

    @Test
    void testFindBookByTitleAndAuthor() {
        Optional<Book> book = bookRepository
                .findBookByTitleAndAuthor("Effective Java: Programming Language Guide", "Joshua Bloch");
        assertThat(book).isPresent();
        assertThat(book.get().getTitle()).isEqualTo("Effective Java: Programming Language Guide");
    }
}

