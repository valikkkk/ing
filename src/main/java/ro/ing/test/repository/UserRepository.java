package ro.ing.test.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.RestController;
import ro.ing.test.model.User;

import java.util.UUID;

@RestController
public interface UserRepository extends JpaRepository<User, UUID> {
}
