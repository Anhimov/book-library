package controllers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import ru.anhimov.library.controllers.PeopleController;
import ru.anhimov.library.models.Person;
import ru.anhimov.library.services.PersonService;
import ru.anhimov.library.util.PersonValidator;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringJUnitConfig(PeopleControllerTest.TestConfig.class)
public class PeopleControllerTest {

    @Autowired
    private PeopleController peopleController;

    private MockMvc mockMvc;

    @Autowired
    private PersonService personService;

    @Autowired
    private PersonValidator personValidator;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(peopleController).build();
        Mockito.reset(personService, personValidator);
    }

    @Test
    void getPeopleShouldReturnPeopleView() throws Exception {
        when(personService.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/people"))
                .andExpect(status().isOk())
                .andExpect(view().name("people/index"))
                .andExpect(model().attributeExists("people"));

        verify(personService, times(1)).findAll();
    }

    @Test
    void getPersonShouldReturnPersonView() throws Exception {
        int personId = 1;
        Person person = new Person();
        when(personService.findById(personId)).thenReturn(person);
        when(personService.findBooksByPersonId(personId)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/people/{id}", personId))
                .andExpect(status().isOk())
                .andExpect(view().name("people/show"))
                .andExpect(model().attributeExists("person"))
                .andExpect(model().attributeExists("books"));

        verify(personService).findById(personId);
        verify(personService).findBooksByPersonId(personId);
    }

    @Test
    void newPersonShouldReturnNewPersonView() throws Exception {
        mockMvc.perform(get("/people/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("people/new"))
                .andExpect(model().attributeExists("person"));
    }

    @Test
    void addPersonWithValidDataShouldRedirect() throws Exception {
        Person person = new Person();
        person.setName("TestPerson");
        person.setAge(18);

        mockMvc.perform(post("/people")
                        .flashAttr("person", person))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/people"));

        verify(personValidator).validate(any(Person.class), any());
        verify(personService).save(any(Person.class));
    }

    @Test
    void addPersonWithInvalidDataShouldReturnNewView() throws Exception {
        Person person = new Person();

        doAnswer(invocation -> {
            Errors errors = invocation.getArgument(1);
            errors.rejectValue("name", "NotEmpty");
            errors.rejectValue("age", "Min");
            return null;
        }).when(personValidator).validate(Mockito.any(Person.class), any());

        mockMvc.perform(post("/people")
                        .flashAttr("person", person))
                .andExpect(status().isOk())
                .andExpect(view().name("people/new"))
                .andExpect(model().attributeHasFieldErrors("person", "name", "age"));

        verify(personValidator).validate(any(Person.class), any());
        verify(personService, times(0)).save(person);
    }

    @Test
    void editPersonShouldReturnEditPersonView() throws Exception {
        int personId = 1;
        Person person = new Person();
        when(personService.findById(personId)).thenReturn(person);

        mockMvc.perform(get("/people/{id}/edit", personId))
                .andExpect(status().isOk())
                .andExpect(view().name("people/edit"))
                .andExpect(model().attributeExists("person"));

        verify(personService).findById(personId);
    }

    @Test
    void updatePersonWithValidDataShouldRedirect() throws Exception {
        int personId = 1;
        Person person = new Person();

        mockMvc.perform(patch("/people/{id}", personId)
                        .flashAttr("person", person))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/people"));

        verify(personService).update(Mockito.eq(personId), any(Person.class));
    }

    @Test
    void updatePersonWithInvalidDataShouldReturnEditView() {
        Person person = new Person();
        person.setName("");
        person.setAge(-1);

        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(person, "person");
        bindingResult.rejectValue("name", "NotEmpty");
        bindingResult.rejectValue("age", "Min");

        String viewName = peopleController.updatePerson(person, bindingResult, 1);

        Assertions.assertEquals("people/edit", viewName);

        verify(personService, never()).update(anyInt(), any(Person.class));
    }

    @Test
    void deletePersonShouldRedirect() throws Exception {
        int personId = 1;

        mockMvc.perform(delete("/people/{id}", personId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/people"));

        verify(personService).delete(Mockito.eq(personId));
    }

    @Configuration
    static class TestConfig {
        @Bean
        public PeopleController peopleController(PersonValidator personValidator, PersonService personService) {
            return new PeopleController(personValidator, personService);
        }

        @Bean
        public PersonService personService() {
            return Mockito.mock(PersonService.class);
        }

        @Bean
        public PersonValidator personValidator() {
            return Mockito.mock(PersonValidator.class);
        }
    }
}



