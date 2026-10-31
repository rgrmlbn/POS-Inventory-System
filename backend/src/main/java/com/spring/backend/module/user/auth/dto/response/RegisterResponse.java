package com.spring.backend.module.user.auth.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class RegisterResponse {

    private Long id;
    private String name;
    private String email;
    private List<String> role;
}
