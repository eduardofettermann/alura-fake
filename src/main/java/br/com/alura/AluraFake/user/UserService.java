package br.com.alura.AluraFake.user;

import br.com.alura.AluraFake.exception.domain.DuplicateUserEmailException;
import br.com.alura.AluraFake.user.dto.NewUserDTO;
import br.com.alura.AluraFake.user.dto.UserListItemDTO;
import br.com.alura.AluraFake.user.model.User;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public void newStudent(NewUserDTO newUser) {
        if (userRepository.existsByEmail(newUser.email())) {
            throw new DuplicateUserEmailException("email");
        }

        User user = newUser.toModel();
        userRepository.save(user);
    }

    public List<UserListItemDTO> listAllUsers() {
        return userRepository.findAll().stream()
                .map(UserListItemDTO::new)
                .toList();
    }
}
