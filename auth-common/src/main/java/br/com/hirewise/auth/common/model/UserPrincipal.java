package br.com.hirewise.auth.common.model;

import java.util.List;

public record UserPrincipal(String userId, List<String> roles) {
}
