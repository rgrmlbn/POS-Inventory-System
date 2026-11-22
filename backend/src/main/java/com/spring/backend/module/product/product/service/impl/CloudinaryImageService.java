package com.spring.backend.module.product.product.service.impl;

import com.cloudinary.Cloudinary;
import com.spring.backend.module.exception.products.products.CloudinaryOperationException;
import com.spring.backend.module.product.product.dto.response.CloudinaryUploadSignatureResponse;
import com.spring.backend.module.properties.CloudinaryProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class CloudinaryImageService {

    private static final Pattern PRODUCT_IMAGE_ID =
            Pattern.compile("^products/[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$");

    private final CloudinaryProperties properties;

    public CloudinaryUploadSignatureResponse createUploadSignature() {
        Cloudinary cloudinary = createCloudinaryClient();
        long timestamp = Instant.now().getEpochSecond();
        String publicId = "products/" + UUID.randomUUID();
        Map<String, Object> parameters = uploadParameters(timestamp, publicId);
        String signature = cloudinary.apiSignRequest(parameters, properties.getApiSecret());

        return CloudinaryUploadSignatureResponse.builder()
                .cloudName(properties.getCloudName())
                .apiKey(properties.getApiKey())
                .uploadPreset(properties.getUploadPreset())
                .publicId(publicId)
                .timestamp(timestamp)
                .signature(signature)
                .build();
    }

    public void deleteImage(String publicId) {
        if (publicId == null || publicId.isBlank()) {
            return;
        }
        if (!PRODUCT_IMAGE_ID.matcher(publicId).matches()) {
            throw new CloudinaryOperationException("Refusing to delete an unmanaged Cloudinary image.");
        }

        Map<?, ?> result;
        try {
            result = createCloudinaryClient().uploader().destroy(
                    publicId,
                    Map.of("invalidate", true)
            );
        } catch (Exception exception) {
            throw new CloudinaryOperationException("Cloudinary image deletion failed.", exception);
        }

        Object status = result.get("result");
        if (!"ok".equals(status) && !"not found".equals(status)) {
            throw new CloudinaryOperationException("Cloudinary did not confirm image deletion.");
        }
    }

    private Map<String, Object> uploadParameters(long timestamp, String publicId) {
        return Map.of(
                "timestamp", timestamp,
                "public_id", publicId,
                "upload_preset", properties.getUploadPreset()
        );
    }

    private Cloudinary createCloudinaryClient() {
        if (isBlank(properties.getCloudName())
                || isBlank(properties.getApiKey())
                || isBlank(properties.getApiSecret())
                || isBlank(properties.getUploadPreset())) {
            throw new CloudinaryOperationException(
                    "Cloudinary is not configured. Set its backend environment variables."
            );
        }

        return new Cloudinary(Map.of(
                "cloud_name", properties.getCloudName(),
                "api_key", properties.getApiKey(),
                "api_secret", properties.getApiSecret()
        ));
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
