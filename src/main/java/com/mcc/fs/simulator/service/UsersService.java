package com.mcc.fs.simulator.service;

import com.mcc.fs.simulator.config.Constants;
import com.mcc.fs.simulator.model.users.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class UsersService {

    private final Map<Integer, User> users;

    public UsersService() {
        users = new Hashtable<>();

        // registering root user
        registerUser(Constants.ROOT_USER);
    }

    public User getUserById(int userId) {
        return users.get(userId);
    }

    public User getUserByUsername(String username) {
        return users
                .values()
                .stream().filter(user -> user.getUsername().equals(username))
                .findFirst().orElse(null);
    }

    public Set<User> getAllUsers() {
        return new HashSet<>(users.values());
    }

    public User registerUser(String username) {
        int userId = Objects.hash(username);

        if (userId < 0) {
            userId *= -1;
        }

        if (users.containsKey(userId)) {
            log.error("user {} already exists", username);
            return users.get(userId);
        }

        User user = User.builder() //
                .id(userId) //
                .username(username) //
                .build();

        users.put(user.getId(), user);

        log.info("User registered {}", user);

        return user;
    }


}
