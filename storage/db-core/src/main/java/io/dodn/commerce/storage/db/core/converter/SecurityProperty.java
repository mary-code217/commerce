package io.dodn.commerce.storage.db.core.converter;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "dodn.storage.core.security")
class SecurityProperty {
    private final String key;
    private final String iv;
}
