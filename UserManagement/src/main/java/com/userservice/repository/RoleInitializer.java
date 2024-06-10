package com.userservice.repository;

import com.userservice.model.ERole;
import com.userservice.model.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class RoleInitializer implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        addRoleIfNotFound(ERole.ROLE_USER);
        addRoleIfNotFound(ERole.ROLE_MODERATOR);
        addRoleIfNotFound(ERole.ROLE_ADMIN);
    }

    private void addRoleIfNotFound(ERole roleName) {
        if (roleRepository.findByName(roleName).isEmpty()) {
            Role role = new Role(roleName);
            roleRepository.save(role);
        }
    }
}
