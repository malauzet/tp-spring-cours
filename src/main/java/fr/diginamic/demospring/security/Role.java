package fr.diginamic.demospring.security;

import org.springframework.security.core.GrantedAuthority;

public class Role implements GrantedAuthority {

    private String nom;

    public Role(String nom) {
        this.nom = nom;
    }

    @Override
    public String getAuthority() {
        return nom;
    }
}
