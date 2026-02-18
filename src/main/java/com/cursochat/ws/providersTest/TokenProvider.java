package com.cursochat.ws.providersTest;

import java.util.Map;

public interface TokenProvider {

    Map<String, String> decode(String token);

}
