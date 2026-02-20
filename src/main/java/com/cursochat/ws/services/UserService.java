package com.cursochat.ws.services;

import com.cursochat.ws.data.User;
import com.cursochat.ws.data.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public List<User> findChatUsers(){
        return  userRepository.findAll();
    }
}
