package br.com.hirewise.auth_service.controller;

import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/test")
public class TestController {
    //Todo: Classe temporaria para testar a funcionalidade dessa api
    @GetMapping("/all")
    public String allAccess() {
        return "Public Content.";
    }
    @GetMapping("/user")
    public String userAccess() {
        return "User Content.";
    }
}