package com.cursochat.ws.providersTest;

import java.security.PublicKey;

public interface KeyProvider {

    PublicKey getPublicKey(String keyId);
}
