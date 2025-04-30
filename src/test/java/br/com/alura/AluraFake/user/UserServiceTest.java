package br.com.alura.AluraFake.user;

import br.com.alura.AluraFake.exception.domain.DuplicateUserEmailException;
import br.com.alura.AluraFake.user.dto.NewUserDTO;
import br.com.alura.AluraFake.user.dto.UserListItemDTO;
import br.com.alura.AluraFake.user.model.UserRole;
import br.com.alura.AluraFake.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private NewUserDTO newUserDTO;
    private User user;

    @BeforeEach
    void setUp() {
        newUserDTO = new NewUserDTO("John Doe", "john.doe@example.com", UserRole.STUDENT, null);
        user = new User("John Doe", "john.doe@example.com", UserRole.STUDENT);
    }

    @Test
    void newUser__should_save_user_when_request_student_is_valid() {
        when(userRepository.existsByEmail(newUserDTO.email())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);

        userService.newStudent(newUserDTO);

        verify(userRepository, times(1)).existsByEmail(newUserDTO.email());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void newUser__should_throw_duplicate_student_email_exception_when_email_exists() {
        when(userRepository.existsByEmail(newUserDTO.email())).thenReturn(true);

        DuplicateUserEmailException exception = assertThrows(DuplicateUserEmailException.class, () -> {
            userService.newStudent(newUserDTO);
        });

        assertEquals("email", exception.getField());
        verify(userRepository, times(1)).existsByEmail(newUserDTO.email());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void listAllUsers__should_return_list_of_user_list_item_dtos() {
        User user1 = new User("John Doe", "john.doe@example.com", UserRole.STUDENT);
        User user2 = new User("Jane Doe", "jane.doe@example.com", UserRole.STUDENT);
        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        List<UserListItemDTO> result = userService.listAllUsers();

        assertEquals(2, result.size());
        assertEquals("John Doe", result.get(0).name());
        assertEquals("john.doe@example.com", result.get(0).email());
        assertEquals("Jane Doe", result.get(1).name());
        assertEquals("jane.doe@example.com", result.get(1).email());
        verify(userRepository, times(1)).findAll();
    }
}