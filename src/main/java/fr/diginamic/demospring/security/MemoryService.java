package fr.diginamic.demospring.security;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MemoryService implements UserDetailsService {

    private final PasswordEncoder passwordEncoder;

    public MemoryService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    private final Map<String, UserDetails> appUsers = new HashMap<>();

    private void initUtilisateurs() {
        if (!appUsers.isEmpty()) return;
        appUsers.put("user1",
                new AppUser("user1", passwordEncoder.encode("user1234"), List.of(new Role("ROLE_USER"))));
        appUsers.put("admin1",
                new AppUser("admin1", passwordEncoder.encode("admin1234"), List.of(new Role("ROLE_ADMIN"))));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        initUtilisateurs();
        UserDetails user = appUsers.get(username);
        if (user == null) {
            throw new UsernameNotFoundException("Utilisateur inconnu : " + username);
        }
        return user;
    }
}