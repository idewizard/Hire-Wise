package br.com.hirewise.auth_service.controller;

import br.com.hirewise.auth_service.model.User;
import br.com.hirewise.auth_service.repository.UserRepository;
import br.com.hirewise.auth_service.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtil jwtUtil;

    @PostMapping("/signin")
    public String authenticateUser(@RequestBody User user){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getUsername(),
                        user.getPassword()
                )
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return jwtUtil.generateToken(userDetails.getUsername());
    }

    @PostMapping("/signup")
    public String registerUser(@RequestBody User user){
        if (userRepository.existsByUsername(user.getUsername())){
            return "Erro: Usuario ja cadastrado";
        }

        //Todo: Cria um novo usuario, mover para service em outro momento
        User newUser = new User(
                null,
                user.getUsername(),
                encoder.encode(user.getPassword())
        );

        userRepository.save(newUser);
        return "Usuario cadastrado com sucesso!";
    }
}
