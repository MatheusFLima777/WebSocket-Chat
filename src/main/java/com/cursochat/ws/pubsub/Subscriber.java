package com.cursochat.ws.pubsub;

import com.cursochat.ws.config.RedisConfig;
import com.cursochat.ws.dtos.ChatMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.data.redis.connection.ReactiveSubscription;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import com.cursochat.ws.handler.WebSocketHandler;
import java.util.logging.Logger;

@Component
public class Subscriber {

    private final static Logger LOGGER = Logger.getLogger(Subscriber.class.getName());

    private final ReactiveStringRedisTemplate redisTemplate;
    private final WebSocketHandler webSocketHandler;

    public Subscriber(ReactiveStringRedisTemplate redisTemplate, WebSocketHandler webSocketHandler) {
        this.redisTemplate = redisTemplate;
        this.webSocketHandler = webSocketHandler;
    }


    @PostConstruct
    private void init() {
        this.redisTemplate
                .listenTo(ChannelTopic.of(RedisConfig.CHAT_MESSAGES_CHANNEL))
                .map(ReactiveSubscription.Message::getMessage)
                .subscribe(this::onChatMessage);

    }

    private void onChatMessage(final String chatMessageSerialized){
        LOGGER.info("Chat message was received");

        try{
            ChatMessage chatMessage = new ObjectMapper().readValue(chatMessageSerialized, ChatMessage.class);
            webSocketHandler.notify(chatMessage);
        } catch (JsonProcessingException e){
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
