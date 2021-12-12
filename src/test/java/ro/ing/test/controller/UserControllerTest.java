package ro.ing.test.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import ro.ing.test.exception.handler.GeneralExceptionHandler;
import ro.ing.test.mapper.UserMapper;
import ro.ing.test.model.User;
import ro.ing.test.repository.UserRepository;
import ro.ing.test.service.UserService;

import javax.servlet.ServletContext;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ro.ing.test.model.User.Role.ADMIN;
import static ro.ing.test.model.User.Role.USER;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {UserControllerTest.Config.class})
@WebAppConfiguration
@EnableWebMvc
class UserControllerTest {
    @Autowired
    private WebApplicationContext webApplicationContext;
    @MockBean
    private UserRepository userRepository;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void test() {
        ServletContext servletContext = webApplicationContext.getServletContext();

        assertNotNull(servletContext);
        assertTrue(servletContext instanceof MockServletContext);
        assertNotNull(webApplicationContext.getBean("userController"));
    }

    @Test
    void GIVEN_users_WHEN_getAllUsers_THEN_return_ok() throws Exception {
        when(userRepository.findAll()).thenReturn(
                List.of(new User("Ion", ADMIN),
                        new User("Gheorghe", USER)));
        mockMvc.perform(get("/api/v1/user")
                    .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].name", containsInAnyOrder("Ion", "Gheorghe")))
                .andExpect(jsonPath("$[*].role", containsInAnyOrder("ADMIN", "USER")))
        ;
    }

    @Test
    void GIVEN_user_WHEN_getAllDetails_THEN_return_dto() throws Exception {
        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(new User("Ion", ADMIN)));
        mockMvc.perform(get("/api/v1/user/"+UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Ion")))
                .andExpect(jsonPath("$.role", is("ADMIN")))
        ;
    }

    @Test
    void GIVEN_no_user_WHEN_getAllDetails_THEN_return_not_found_status() throws Exception {
        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.empty());
        mockMvc.perform(get("/api/v1/user/"+UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statusCode", is(404)))
                .andExpect(jsonPath("$.message", containsString("Not found any object with id")))
        ;
    }

    @TestConfiguration
    static class Config{
        @Bean
        UserController userController(UserRepository userRepository) {
            return new UserController(new UserService(userRepository, new UserMapper()));
        }

        @Bean
        GeneralExceptionHandler generalExceptionHandler() {
            return new GeneralExceptionHandler();
        }

    }
}