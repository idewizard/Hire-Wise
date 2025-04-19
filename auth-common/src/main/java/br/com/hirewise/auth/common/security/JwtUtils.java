package br.com.hirewise.auth.common.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;


@Component
public class JwtUtils {

    @Value("${jwt.secret}")
    private String jwtSecret;

    private SecretKey key;

    // Inicializa a chave depois da classe ser instanciada e a injecao do jwtSecret
    // evitando a criacao repetida da chave e melhorando performance
    @PostConstruct
    public void init(){
        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }


    public Claims parseToken(String token){
        JwtParser parser = Jwts.parser()
                .verifyWith(getSigningKey(jwtSecret))
                .build();

        Jwt<?,?> jwt = parser.parse(token);
        return (Claims) jwt.getPayload();
    }

    public String extractUserId(String token){
        return parseToken(token).getSubject();
    }

    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token) {
        Claims claims = parseToken(token);
        return (List<String>) claims.get("roles", List.class);
    }

    private static SecretKey getSigningKey(String jwt) {
        byte[] keyBytes = Base64.getDecoder().decode(jwt);
        return Keys.hmacShaKeyFor(keyBytes);
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
