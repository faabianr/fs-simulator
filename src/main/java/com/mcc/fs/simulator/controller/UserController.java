package com.mcc.fs.simulator.controller;

import com.mcc.fs.simulator.model.network.UserLoginRequest;
import com.mcc.fs.simulator.model.users.User;
import com.mcc.fs.simulator.service.UsersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final UsersService usersService;

    public UserController(UsersService usersService) {
        this.usersService = usersService;
    }

    @GetMapping
    public ResponseEntity<Set<User>> getUsers() {
        Set<User> users = usersService.getAllUsers();
        log.info("Returning all users: {}", users);
        return ResponseEntity.ok(users);
    }

    @PostMapping("login")
    public ResponseEntity<User> login(@RequestBody UserLoginRequest userLoginRequest) {
        User user = usersService.registerUser(userLoginRequest.getUsername());
        log.info("Login of user: {}", user);
        return ResponseEntity.ok(user);
    }

}
