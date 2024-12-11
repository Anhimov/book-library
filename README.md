# Book Library

This is a Spring-based project for managing a book library, allowing CRUD operations on books and people. The application uses Thymeleaf for view rendering, Hibernate for ORM, and PostgreSQL for the production database, with H2 for testing. The project includes the following features:

- **CRUD operations** for managing books and people
- **Search functionality** for books by title
- **Book assignment** to people
- **Spring Profiles** for managing different environments (production and testing)

## Features

### People Management
- View, add, edit, and delete people
- Assign books to people

### Book Management
- View, add, edit, and delete books
- Search books by title
- Assign and release books to/from people

## Technologies Used
- **Spring Framework** (Spring Core, Spring MVC, Spring Data JPA, Spring ORM)
- **Thymeleaf** for templating
- **Hibernate** for ORM
- **PostgreSQL** for production database
- **H2** for testing database
- **JUnit**, **Mockito**, **AssertJ** for testing
- **Bootstrap** for styling in views

## Legacy DAO Classes
The BookDAO and PersonDAO classes in this project use JdbcTemplate for database operations. These classes are kept in the project to show my ability to work with both JdbcTemplate and JPA.

While the application now uses JPA and Spring Data for database access, these DAO classes demonstrate how the project worked before switching to JPA. You can check these classes to see examples of custom SQL queries and manual database handling.

## Setup

### Prerequisites
- JDK 11
- Maven
- PostgreSQL (for production environment)

### Running Locally
To run the application locally, follow these steps:

1. Clone the repository:
   ```bash
   git clone https://github.com/your-username/book-library.git
   cd book-library
   

2. Set up the database:
    - Ensure PostgreSQL is installed and running.
    - Create a database for the project:
      ```sql
      CREATE DATABASE book_library;
      ```

3. Configure the application:
    - In `src/main/resources/application.properties`, configure your PostgreSQL connection:
      ```properties
      spring.datasource.url=jdbc:postgresql://localhost:5432/book_library
      spring.datasource.username=your-username
      spring.datasource.password=your-password
      ```

4. Run the application:
    - Use the following command to start the application:
      ```bash
      mvn spring-boot:run
      ```

5. Access the application:
    - The app will be available at `http://localhost:8080/`.

### Profiles
The project uses Spring profiles for different environments:

- **Test profile**: For running tests with H2 database. Use the following command to run tests:
  ```bash
  mvn test
  ```

- **Prod profile**: For the production environment, with PostgreSQL. To run the application in the production profile, use the following command:
  ```bash
  mvn spring-boot:run -Dspring.profiles.active=prod
  ```

### Database Initialization

The project includes SQL scripts to initialize and populate the database:

- `db_init.sql`:
    - Creates the `person` and `book` tables with appropriate constraints.

- `db_populate.sql`:
    - Populates the `person` and `book` tables with sample data for testing.

### Testing

- The project includes tests, which can be run with Maven:
  ```bash
  mvn test
  ```

- The test profile uses an H2 database for testing purposes, while the production profile uses PostgreSQL.

## API Endpoints

- **People Endpoints**
    - `GET /people`: List all people
    - `GET /people/{id}`: View a specific person
    - `POST /people`: Create a new person
    - `PATCH /people/{id}`: Update a person
    - `DELETE /people/{id}`: Delete a person

- **Books Endpoints**
    - `GET /books`: List all books
    - `GET /books/{id}`: View a specific book
    - `POST /books`: Create a new book
    - `PATCH /books/{id}`: Update a book
    - `DELETE /books/{id}`: Delete a book
    - `PATCH /books/{id}/assign`: Assign a book to a person
    - `PATCH /books/{id}/release`: Release a book from a person
    - `POST /books/search`: Search books by title