package dev.scaler.abin.e_comuser_service.repositories;

import dev.scaler.abin.e_comuser_service.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoleRepository extends JpaRepository<Role, Long> {
    List<Role> findAllByIdIn(List<Long> roleIds);
}
