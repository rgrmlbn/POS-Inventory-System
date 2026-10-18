package com.spring.backend.module.user.user.dto.response;


import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
public class UserResponse {

    private Long id;
    private String name;
    private String email;
    private java.util.Set<String> roles;
    private java.util.Set<String> permissions;

}
