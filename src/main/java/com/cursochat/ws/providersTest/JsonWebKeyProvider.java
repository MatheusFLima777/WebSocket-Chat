package com.cursochat.ws.providersTest;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkException;
import com.auth0.jwk.UrlJwkProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.PublicKey;

@Component
public class JsonWebKeyProvider implements KeyProvider {

    private UrlJwkProvider provider;

    public JsonWebKeyProvider(@Value("${app.auth.jwks-url}") String jwksUrl) {
        try {
            this.provider = new UrlJwkProvider(new URL(jwksUrl));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    @Cacheable("public-key")
    @Override
    public PublicKey getPublicKey(String keyId) {
        try {
            Jwk jwk = provider.get(keyId);
            return jwk.getPublicKey();
        } catch (JwkException e) {
            throw new RuntimeException("Erro ao obter chave pública do JWKS", e);
        }
    }
}
