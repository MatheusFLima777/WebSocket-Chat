package com.cursochat.ws.services;

import com.cursochat.ws.data.User;
import com.cursochat.ws.data.UserRepository;
import com.cursochat.ws.providers.TokenProvider;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class TicketService {

    private final RedisTemplate<String, String> redisTemplate;
    private final TokenProvider tokenProvider;
    private final UserRepository userRepository;

    public TicketService(RedisTemplate<String, String> redisTemplate,
                         TokenProvider tokenProvider,
                         UserRepository userRepository) {
        this.redisTemplate = redisTemplate;
        this.tokenProvider = tokenProvider;
        this.userRepository = userRepository;
    }

    public String buildAndSaveTicket(String token) {

        if (token == null || token.isBlank()) {
            throw new RuntimeException("Missing Token");
        }

        String ticket = UUID.randomUUID().toString();

        Map<String, String> claims = tokenProvider.decode(token);

        String userId = claims.get("sub");

        if (userId == null || userId.isBlank()) {
            throw new RuntimeException("Token does not contain 'sub'");
        }

        redisTemplate.opsForValue()
                .set(ticket, userId, Duration.ofMinutes(2));

        saveOrUpdateUser(claims);

        return ticket;
    }

    private void saveOrUpdateUser(Map<String, String> claims) {

        String userId = claims.get("sub");
        String name = claims.getOrDefault("name", "Unknown");
        String picture = claims.getOrDefault("picture", "");

        Optional<User> existingUser = userRepository.findById(userId);

        if (existingUser.isPresent()) {

            User existing = existingUser.get();

            boolean nameChanged = !java.util.Objects.equals(existing.name(), name);
            boolean pictureChanged = !java.util.Objects.equals(existing.picture(), picture);

            if (nameChanged || pictureChanged) {
                User updatedUser = new User(userId, name, picture);
                userRepository.save(updatedUser);
            }

        } else {
            userRepository.save(new User(userId, name, picture));
        }
    }

    public Optional<String> getUserIdByTicket(String ticket) {
        return Optional.ofNullable(
                redisTemplate.opsForValue().getAndDelete(ticket)
        );
    }
}