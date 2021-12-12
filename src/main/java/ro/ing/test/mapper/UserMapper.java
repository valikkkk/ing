 package ro.ing.test.mapper;

import org.springframework.stereotype.Component;
import ro.ing.test.dto.response.UserDto;
import ro.ing.test.model.User;

@Component
public class UserMapper {

    public UserDto toDto(User user) {
        return UserDto.builder()
                .name(user.getName())
                .role(user.getRole().name())
                .build();
    }
}
