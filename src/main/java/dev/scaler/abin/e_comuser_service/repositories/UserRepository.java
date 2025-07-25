package dev.scaler.abin.e_comuser_service.repositories;

import dev.scaler.abin.e_comuser_service.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
}
