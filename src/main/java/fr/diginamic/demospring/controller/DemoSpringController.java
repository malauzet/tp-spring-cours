package fr.diginamic.demospring.controller;

import fr.diginamic.demospring.service.DemoSpringService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hello")
public class DemoSpringController {

    @Autowired
    private DemoSpringService helloService;

    @GetMapping
    public String direHello() {
        return helloService.salutations();
    }
}
