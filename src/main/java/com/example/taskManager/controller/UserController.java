package com.example.taskManager.controller;

import com.example.taskManager.common.exception.Response;
import com.example.taskManager.model.DTO.request.ChangePasswordRequest;
import com.example.taskManager.model.DTO.request.UserInforRequest;
import com.example.taskManager.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/details")
    public Response<?> getUserDetails(Authentication authentication) {
        return Response.success(userService.getUserByEmail(authentication));
    }

    @PostMapping("/change-password")
    public ResponseEntity<Response<?>> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest,
                                                      Authentication authentication) {
        return ResponseEntity.ok(
                Response.success(userService.changePassword(changePasswordRequest, authentication))
        );
    }

    @PutMapping("/update-infor")
    public ResponseEntity<Response<?>> updateUserInformation(@RequestBody UserInforRequest userInforRequest,
                                                             Authentication authentication) {
        return ResponseEntity.ok(
                Response.success(userService.updateUserInformation(userInforRequest, authentication))
        );
    }
}
