package fr.diginamic.demospring.security;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "app_user")
public class AppUser implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "username", unique = true)
    private String username;

    @Column(name = "password")
    private String password;

    @OneToMany(mappedBy = "appUser", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Role> roles = new ArrayList<>();

    public AppUser() {
    }

    public AppUser(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public void addRole(Role role) {
        role.setAppUser(this);
        roles.add(role);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() { return roles; }

    @Override
    public String getPassword() { return password; }

    @Override
    public String getUsername() { return username; }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}