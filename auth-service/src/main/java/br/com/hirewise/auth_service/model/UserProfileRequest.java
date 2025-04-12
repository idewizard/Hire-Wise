package br.com.hirewise.auth_service.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class UserProfileRequest {
    private Long userId;
    private String username;
    private List<Role> roles;
}
