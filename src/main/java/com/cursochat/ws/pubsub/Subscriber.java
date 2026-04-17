package com.cursochat.ws.pubsub;

import com.cursochat.ws.config.RedisConfig;
import com.cursochat.ws.dtos.ChatMessage;
import com.cursochat.ws.handler.WebSocketHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.connection.ReactiveSubscription;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
public class Subscriber {

    private static final Logger LOGGER = Logger.getLogger(Subscriber.class.getName());

    private final ReactiveStringRedisTemplate redisTemplate;
    private final WebSocketHandler webSocketHandler;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Subscriber(ReactiveStringRedisTemplate redisTemplate,
                      WebSocketHandler webSocketHandler) {
        this.redisTemplate = redisTemplate;
        this.webSocketHandler = webSocketHandler;
    }

    /**
     * Inicia o subscriber somente depois que a aplicação estiver pronta
     */
    @EventListener(ApplicationReadyEvent.class)
    public void startSubscriber() {
        try {
            redisTemplate
                    .listenTo(ChannelTopic.of(RedisConfig.CHAT_MESSAGES_CHANNEL))
                    .map(ReactiveSubscription.Message::getMessage)
                    .subscribe(
                            this::onChatMessage,
                            error -> LOGGER.severe("Erro no subscriber: " + error.getMessage())
                    );

            LOGGER.info(" Subscriber conectado ao Redis com sucesso");

        } catch (Exception e) {
            LOGGER.severe(" Falha ao iniciar subscriber Redis: " + e.getMessage());
        }
    }

    private void onChatMessage(final String chatMessageSerialized) {
        LOGGER.info("📩 Mensagem recebida do Redis");

        try {
            ChatMessage chatMessage =
                    objectMapper.readValue(chatMessageSerialized, ChatMessage.class);

            webSocketHandler.notify(chatMessage);

        } catch (JsonProcessingException e) {
            LOGGER.severe("Erro ao deserializar mensagem: " + e.getMessage());
        }
    }
}