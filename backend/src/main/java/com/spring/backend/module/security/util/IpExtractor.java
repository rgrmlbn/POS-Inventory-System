
package com.spring.backend.module.security.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class IpExtractor {

    /**
     * Resolves the client IP from the first {@code X-Forwarded-For} entry,
     * falling back to the direct connection address when the header is absent.
     * The forwarded header is only trustworthy when set by a trusted proxy.
     *
     * @return the client IP, or {@code null} when there is no active HTTP request
     */
    public String getClientIp() {
        // → Get the current HTTP request.
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        // → Return null if there is no active HTTP request.
        if (attributes == null) {
            return null;
        }

        HttpServletRequest request = attributes.getRequest();

        // → Check whether the request contains a forwarded client IP.
        String forwardedIp = request.getHeader("X-Forwarded-For");

        if (forwardedIp != null && !forwardedIp.isBlank()) {
            // → Return the first IP address in the forwarded header.
            return forwardedIp.split(",")[0].trim();
        }

        // → Fall back to the IP address of the direct connection.
        return request.getRemoteAddr();
    }
}
