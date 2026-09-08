package fr.diginamic.demospring.security;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserSeeder implements CommandLineRunner {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    public UserSeeder(AppUserRepository appUserRepository, PasswordEncoder passwordEncoder) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (appUserRepository.findByUsername("user1").isEmpty()) {
            AppUser user = new AppUser("user1", passwordEncoder.encode("user1234"));
            user.addRole(new Role("ROLE_USER"));
            appUserRepository.save(user);
        }
        if (appUserRepository.findByUsername("admin1").isEmpty()) {
            AppUser admin = new AppUser("admin1", passwordEncoder.encode("admin1234"));
            admin.addRole(new Role("ROLE_ADMIN"));
            appUserRepository.save(admin);
        }
    }
}