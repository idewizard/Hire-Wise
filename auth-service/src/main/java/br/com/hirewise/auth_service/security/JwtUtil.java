package br.com.hirewise.auth_service.security;

import br.com.hirewise.auth_service.model.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private int jwtExpirationMs;

    private SecretKey key;

    // Inicializa a chave depois da classe ser instanciada e a injecao do jwtSecret
    // evitando a criacao repetida da chave e melhorando performance
    @PostConstruct
    public void init(){
        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    // Gera o Token JWT
    public String generateToken(User user){
        return Jwts.builder()
                .subject(user.getUsername())
                .claim("roles", user.getRoles()) //busca as permissoes
                .issuedAt(new Date())
                .expiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }

    public List<String> getRolesFromToken(String token) {
        return ((List<?>) Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("roles", List.class))
                .stream()
                .map(Object::toString)
                .collect(Collectors.toList());
    }

    // Pega o username do token jwt
    public String getUsernameFromToken(String token){
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    // Valida o Token JWT
    public boolean validateJwtToken(String token){
        try{
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        } catch (SecurityException e){
            System.out.println("Assinatura JWT Invalida: " + e.getMessage());
        } catch (MalformedJwtException e){
            System.out.println("Token JWT Invalido: " + e.getMessage());
        } catch (ExpiredJwtException e){
            System.out.println("Token JWT vencido: " + e.getMessage());
        } catch (UnsupportedJwtException e){
            System.out.println("Token JWT nao suportado: " + e.getMessage());
        } catch (IllegalArgumentException e){
            System.out.println("Claims do token JWT estao vazias: " + e.getMessage());
        }

        return false;
    }

}
