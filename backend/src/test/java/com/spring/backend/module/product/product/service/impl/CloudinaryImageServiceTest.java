package com.spring.backend.module.product.product.service.impl;

import com.cloudinary.Cloudinary;
import com.spring.backend.module.exception.products.products.CloudinaryOperationException;
import com.spring.backend.module.properties.CloudinaryProperties;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CloudinaryImageServiceTest {

    @Test
    void ignoresBlankPublicId() {
        CloudinaryImageService service = new CloudinaryImageService(new CloudinaryProperties());

        assertDoesNotThrow(() -> service.deleteImage(" "));
    }

    @Test
    void refusesToDeleteAnUnmanagedPublicId() {
        CloudinaryImageService service = new CloudinaryImageService(new CloudinaryProperties());

        assertThrows(
                CloudinaryOperationException.class,
                () -> service.deleteImage("other-folder/image")
        );
    }

    @Test
    void signsOnlyTheExpectedPresetTimestampAndServerGeneratedPublicId() {
        CloudinaryProperties properties = new CloudinaryProperties();
        properties.setCloudName("test-cloud");
        properties.setApiKey("test-api-key");
        properties.setApiSecret("test-api-secret");
        properties.setUploadPreset("signed-products");

        CloudinaryImageService service = new CloudinaryImageService(properties);
        var response = service.createUploadSignature();

        assertTrue(response.getPublicId().matches(
                "^products/[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$"
        ));
        assertEquals("test-cloud", response.getCloudName());
        assertEquals("test-api-key", response.getApiKey());
        assertEquals("signed-products", response.getUploadPreset());

        Cloudinary cloudinary = new Cloudinary(Map.of(
                "cloud_name", properties.getCloudName(),
                "api_key", properties.getApiKey(),
                "api_secret", properties.getApiSecret()
        ));
        Map<String, Object> signedParameters = Map.of(
                "timestamp", response.getTimestamp(),
                "public_id", response.getPublicId(),
                "upload_preset", response.getUploadPreset()
        );
        assertEquals(
                cloudinary.apiSignRequest(signedParameters, properties.getApiSecret()),
                response.getSignature()
        );
    }
}
