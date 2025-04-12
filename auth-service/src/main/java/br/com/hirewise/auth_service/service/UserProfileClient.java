package br.com.hirewise.auth_service.service;

import br.com.hirewise.auth_service.model.User;
import br.com.hirewise.auth_service.model.UserProfileRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class UserProfileClient {

    private final WebClient webClient;

    @Value("${user.service.url}")
    private String userServiceUrl;

    public UserProfileClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public void create(User user, String jwt) {
        UserProfileRequest request = new UserProfileRequest(user.getId(), user.getUsername(), user.getRoles());

        webClient.post()
                .uri(userServiceUrl + "/internal/profiles")
                .header(HttpHeaders.AUTHORIZATION, jwt)
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatusCode::isError, response ->
                        response.bodyToMono(String.class)
                        .flatMap(error -> Mono.error(new RuntimeException("Erro ao criar usuario, erro: " + error)))
                )
                .toBodilessEntity()
                .block();
    }

}
