package ru.anhimov.library.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.anhimov.library.models.Book;
import ru.anhimov.library.models.Person;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Integer> {
    @Query("SELECT p FROM Person p JOIN Book b ON p.id = b.owner.id WHERE b.id = :bookId")
    Optional<Person> findBookOwner(@Param("bookId") int bookId);

    List<Book> findBookByTitleLikeIgnoreCase(String query);

    Optional<Book> findBookByTitleAndAuthor(String title, String author);
}
