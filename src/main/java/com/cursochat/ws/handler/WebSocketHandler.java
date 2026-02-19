package com.cursochat.ws.handler;

import com.cursochat.ws.services.TicketService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

@Component
public class WebSocketHandler extends TextWebSocketHandler {

    private final static Logger LOGGER = Logger.getLogger(WebSocketHandler.class.getName());

    private final TicketService ticketService;

    private final Map<String, WebSocketSession> sessions;

    public WebSocketHandler(TicketService ticketService){
        this.ticketService = ticketService;
        sessions = new ConcurrentHashMap<>();
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        LOGGER.info("[afterConnectionEstablished] session id " + session.getId());

       Optional<String> ticket = ticketOf(session);
       if(ticket.isEmpty() || ticket.get().isBlank()){
           LOGGER.warning("Sessão" + session.getId() + " está Vazia");
           close(session, CloseStatus.POLICY_VIOLATION);
           return;
       }

       Optional<String> userId = ticketService.getUserIdByTicket(ticket.get());
       if(userId.isEmpty()){
           LOGGER.warning("sessão" + session.getId() + "está Inválida");
           close(session, CloseStatus.POLICY_VIOLATION);
           return;
       }

       sessions.put(userId.get(), session);
       LOGGER.info("Sessao" + session.getId() + "foi vinculada ao usuário" + userId.get());
    }

    private void close(WebSocketSession session, CloseStatus status){
        try {
            session.close(status);
        } catch (IOException e){
            e.printStackTrace();
        }
    }
    private Optional<String> ticketOf(WebSocketSession session) {
        return Optional
                .ofNullable(session.getUri())
                .map(UriComponentsBuilder::fromUri)
                .map(UriComponentsBuilder::build)
                .map(UriComponents::getQueryParams)
                .map(it -> it.get("ticket"))
                .flatMap(it -> it.stream().findFirst())
                .map(String::trim);

    }


    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message){
        LOGGER.info("[handleTextMessage] message" + message.getPayload());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status){
        LOGGER.info("[afterConnectionClosed] session id" + session.getId());
    }
}
