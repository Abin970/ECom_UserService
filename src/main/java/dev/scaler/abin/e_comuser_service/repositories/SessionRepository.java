package dev.scaler.abin.e_comuser_service.repositories;

import dev.scaler.abin.e_comuser_service.models.Session;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SessionRepository extends JpaRepository<Session, Long> {

    Optional<Session> findByTokenAndUser_Id(String token, Long userId);
}
