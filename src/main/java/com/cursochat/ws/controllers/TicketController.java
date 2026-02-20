package com.cursochat.ws.controllers;


import com.cursochat.ws.services.TicketService;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("v1/ticket")
@CrossOrigin
public class TicketController {


    private final TicketService ticketService;

    public TicketController(TicketService ticketService){
        this.ticketService = ticketService;
    }

    @PostMapping
    public Map<String, String> buildTicket(
        @RequestHeader(HttpHeaders.AUTHORIZATION)
                String authorization
    ){
        String token = Optional
                .ofNullable(authorization)
                .filter(it -> it.startsWith("Bearer "))
                .map(it -> it.substring(7))
                .orElse("");

        String ticket = ticketService.buildAndSaveTicket(token);
        return Map.of("ticket", ticket);
    }
}
