package ro.ing.test.service;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import ro.ing.test.dto.response.UserDto;
import ro.ing.test.exception.NotFoundException;
import ro.ing.test.mapper.UserMapper;
import ro.ing.test.model.User;
import ro.ing.test.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.util.List.of;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static ro.ing.test.model.User.Role.ADMIN;
import static ro.ing.test.model.User.Role.USER;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @InjectMocks
    private UserService userService;

    @Test
    public void GIVEN_one_user_WHEN_getAllUsers_THEN_return_dto() {
        User user1 = new User("Ion", ADMIN);
        User user2 = new User("Gheorghe", USER);
        when(userRepository.findAll()).thenReturn(of(user1, user2));
        UserDto userDto1 = new UserDto("Ion", "ADMIN");
        UserDto userDto2 = new UserDto("Gheorghe", "USER");
        when(userMapper.toDto(any(User.class)))
                .thenReturn(userDto1).thenReturn(userDto2);

        List<UserDto> allUsers = userService.getAllUsers();
        assertArrayEquals(new UserDto[]{userDto1, userDto2}, allUsers.toArray());
        verify(userRepository).findAll();
        ArgumentCaptor<User> ac = ArgumentCaptor.forClass(User.class);
        verify(userMapper, times(2)).toDto(ac.capture());
        assertArrayEquals(new User[]{user1, user2}, ac.getAllValues().toArray());
        verifyNoMoreInteractions(userRepository, userMapper);
    }

    @Test
    public void GIVEN_noUser_WHEN_getUserDetails_THEN_throwError() {
        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.empty());
        UUID uuid = UUID.randomUUID();
        assertThrows(NotFoundException.class, () -> userService.getUserDetails(uuid));
        verify(userRepository).findById(uuid);
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(userMapper);
    }

    @Test
    public void GIVEN_oneUser_WHEN_getUserDetails_THEN_return_ok() {
        User user = new User("Ion", ADMIN);
        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(user));
        UserDto userDto = new UserDto("Ion","ADMIN");
        when(userMapper.toDto(any(User.class))).thenReturn(userDto);

        UUID uuid = UUID.randomUUID();
        UserDto result = userService.getUserDetails(uuid);

        verify(userRepository).findById(uuid);
        verify(userMapper).toDto(user);
        assertEquals(userDto, result);
        verifyNoMoreInteractions(userRepository, userMapper);
    }
}