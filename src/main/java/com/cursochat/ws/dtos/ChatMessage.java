package com.cursochat.ws.dtos;

import com.cursochat.ws.data.User;

public record ChatMessage(User from, User to, String text) {
}
