package com.cursochat.ws.pubsub;

import com.cursochat.ws.config.RedisConfig;
import com.cursochat.ws.data.User;
import com.cursochat.ws.data.UserRepository;
import com.cursochat.ws.dtos.ChatMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
public class Publisher {

    private final static Logger LOGGER = Logger.getLogger(Publisher.class.getName());

    private final UserRepository userRepository;
    private final ReactiveStringRedisTemplate redisTemplate;

    public Publisher(UserRepository userRepository,
                     ReactiveStringRedisTemplate redisTemplate){
        this.userRepository = userRepository;
        this.redisTemplate = redisTemplate;
    }

    public void publishChatMessage(String userIdFrom, String userIdTo, String text) throws JsonProcessingException{
        User from = userRepository.findById(userIdFrom).orElseThrow();
        User to = userRepository.findById(userIdTo).orElseThrow();
        ChatMessage chatMessage = new ChatMessage(from, to, text);
        String chatMessageSerialized = new ObjectMapper().writeValueAsString(chatMessage);
        redisTemplate
                .convertAndSend(RedisConfig.CHAT_MESSAGES_CHANNEL, chatMessageSerialized)
                .subscribe();
        LOGGER.info("Chat Message was published");
    }

}
