package com.abach42.superhero.config.security;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConditionalOnProperty(name = "com.abach42.superhero.security.jwt.symmetric",
        havingValue = "false")
@ConfigurationProperties(prefix = "com.abach42.superhero.security.jwt")
public record RsaKeyProperties(RSAPublicKey publicKey, RSAPrivateKey privateKey) {

}