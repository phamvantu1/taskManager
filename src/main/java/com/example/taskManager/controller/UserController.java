package com.example.taskManager.controller;

import com.example.taskManager.common.exception.Response;
import com.example.taskManager.repository.UserRepository;
import com.example.taskManager.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/details")
    public Response<?> getUserDetails(@RequestParam String  email) {
        return Response.success(userService.getUserByEmail(email));
    }
}
