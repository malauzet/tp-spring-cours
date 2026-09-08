package fr.diginamic.demospring.security;

import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtils {

    private SecurityUtils() {
        // classe utilitaire, pas d'instanciation
    }

    /** @return le username de l'utilisateur actuellement authentifié */
    public static String getCurrentUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}