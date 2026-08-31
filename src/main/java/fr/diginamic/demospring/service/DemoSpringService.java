package fr.diginamic.demospring.service;

import org.springframework.stereotype.Service;

@Service
public class DemoSpringService {

    public String salutations() {
        return "Je suis la classe de service et je vous dis Bonjour";
    }
}
