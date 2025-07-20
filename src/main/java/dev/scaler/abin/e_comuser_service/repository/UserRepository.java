package dev.scaler.abin.e_comuser_service.repository;

import dev.scaler.abin.e_comuser_service.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
}
