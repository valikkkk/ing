package ro.ing.test.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ro.ing.test.dto.response.UserDto;
import ro.ing.test.exception.NotFoundException;
import ro.ing.test.mapper.UserMapper;
import ro.ing.test.repository.UserRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    public UserDto getUserDetails(UUID id) {
        return userRepository.findById(id)
                .map(userMapper::toDto)
                .orElseThrow(()-> new NotFoundException("Not found any object with id:"+id));
    }
}
