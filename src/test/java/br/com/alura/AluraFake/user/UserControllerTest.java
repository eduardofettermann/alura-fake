package br.com.alura.AluraFake.user;

import br.com.alura.AluraFake.AluraFakeApplication;
import br.com.alura.AluraFake.exception.domain.DuplicateUserEmailException;
import br.com.alura.AluraFake.infra.security.SecurityConfiguration;
import br.com.alura.AluraFake.infra.security.TokenService;
import br.com.alura.AluraFake.user.dto.NewUserDTO;
import br.com.alura.AluraFake.user.dto.UserListItemDTO;
import br.com.alura.AluraFake.user.model.UserRole;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(value = SpringExtension.class)
@ContextConfiguration(classes = {
        AluraFakeApplication.class,
        SecurityConfiguration.class}
)
@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private TokenService tokenService;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @WithMockUser(username = "instructor", roles = {"INSTRUCTOR"})
    @Test
    void newUser__should_return_bad_request_when_email_is_blank() throws Exception {
        NewUserDTO newUserDTO = new NewUserDTO("John Doe", "john.doe@example.com", UserRole.STUDENT, null);

        mockMvc.perform(post("/user/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUserDTO)))
                .andExpect(status().isCreated());

        verify(userService, times(1)).newStudent(newUserDTO);
    }

    @WithMockUser(username = "instructor", roles = {"INSTRUCTOR"})
    @Test
    void newUser__should_return_bad_request_when_email_is_invalid() throws Exception {
        NewUserDTO newUserDTO = new NewUserDTO("John Doe", "invalid-email", UserRole.STUDENT, null);

        mockMvc.perform(post("/user/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUserDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field").value("email"))
                .andExpect(jsonPath("$[0].message").isNotEmpty());
    }

    @WithMockUser(username = "instructor", roles = {"INSTRUCTOR"})
    @Test
    void newUser__should_return_unprocessable_entity_when_email_already_exists() throws Exception {
        NewUserDTO newUserDTO = new NewUserDTO("John Doe", "john.doe@example.com", UserRole.STUDENT, null);
        doThrow(new DuplicateUserEmailException("email")).when(userService).newStudent(newUserDTO);

        mockMvc.perform(post("/user/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUserDTO)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.field").value("email"))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @WithMockUser(username = "instructor", roles = {"INSTRUCTOR"})
    @Test
    void listAllUsers__should_return_ok_and_list_users() throws Exception {
        UserListItemDTO user1 = new UserListItemDTO("John Doe", "john.doe@example.com", UserRole.STUDENT);
        UserListItemDTO user2 = new UserListItemDTO("Jane Doe", "jane.doe@example.com", UserRole.STUDENT);
        when(userService.listAllUsers()).thenReturn(List.of(user1, user2));

        mockMvc.perform(get("/user/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("John Doe"))
                .andExpect(jsonPath("$[0].email").value("john.doe@example.com"))
                .andExpect(jsonPath("$[1].name").value("Jane Doe"))
                .andExpect(jsonPath("$[1].email").value("jane.doe@example.com"));

        verify(userService, times(1)).listAllUsers();
    }
}