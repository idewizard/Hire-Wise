package br.com.hirewise.auth_service.controller;

import br.com.hirewise.auth_service.model.User;
import br.com.hirewise.auth_service.repository.UserRepository;
import br.com.hirewise.auth_service.security.JwtUtil;
import br.com.hirewise.auth_service.service.UserProfileClient;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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

    @Autowired
    UserProfileClient userProfileClient;

    @PostMapping("/signin")
    public String authenticateUser(@RequestBody User user){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getUsername(),
                        user.getPassword()
                )
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        User fullUserDetails = userRepository.findByUsername(userDetails.getUsername());

        if( fullUserDetails == null){
            System.out.println("Usuario nao encontrado");
            throw new UsernameNotFoundException("Usuario nao encontrado");
        }

        return jwtUtil.generateToken(fullUserDetails);
    }


    //Todo: Cria um novo usuario, INCOMPLETO AINDA
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody User user, HttpServletRequest request){
        if (userRepository.existsByUsername(user.getUsername())){
            return ResponseEntity.badRequest().body("Usuario Ja cadastrado");
        }




        //TODO mover para uma service
        User newUser = new User(
                null,
                user.getUsername(),
                encoder.encode(user.getPassword()),
                user.getRoles()
        );
        userRepository.save(newUser);

        String jwt = request.getHeader(HttpHeaders.AUTHORIZATION);

        userProfileClient.create(user, jwt);

        return ResponseEntity.ok().body("Usuario Criado com sucesso");
    }
}
