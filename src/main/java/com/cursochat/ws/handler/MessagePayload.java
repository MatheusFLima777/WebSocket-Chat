package com.cursochat.ws.handler;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record MessagePayload(String to, String text) {
}
