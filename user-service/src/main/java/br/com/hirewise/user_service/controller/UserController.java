package br.com.hirewise.user_service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @GetMapping
    public String get(){
        return "Oba oba oba ae";
    }

}
