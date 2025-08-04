package dev.scaler.abin.e_comuser_service.services;

import dev.scaler.abin.e_comuser_service.models.Role;
import dev.scaler.abin.e_comuser_service.repositories.RoleRepository;
import org.springframework.stereotype.Service;

@Service
public class RoleService implements IRoleService {
    private RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public Role createRole(String name) {
        Role role = new Role();
        role.setRole(name);

        return roleRepository.save(role);
    }
}
