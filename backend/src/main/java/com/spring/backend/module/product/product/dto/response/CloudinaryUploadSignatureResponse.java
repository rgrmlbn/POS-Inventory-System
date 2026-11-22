package com.spring.backend.module.product.product.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CloudinaryUploadSignatureResponse {

    private String cloudName;
    private String apiKey;
    private String uploadPreset;
    private String publicId;
    private long timestamp;
    private String signature;
}
