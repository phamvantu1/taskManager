package com.example.taskManager.controller;

import com.example.taskManager.common.exception.Response;
import com.example.taskManager.model.DTO.request.ChangePasswordRequest;
import com.example.taskManager.model.DTO.request.UserInforRequest;
import com.example.taskManager.model.DTO.response.UserInfoResponse;
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

    @GetMapping("/get-all-users")
    public ResponseEntity<Response<?>> getAllUsers(@RequestParam(name = "page", defaultValue = "0") int page,
                                                   @RequestParam(name = "size", defaultValue = "10") int size,
                                                   @RequestParam(name = "departmentId", required = false) Long departmentId) {
        return ResponseEntity.ok(Response.success(userService.getAllUsers(page, size, departmentId)));
    }

    @GetMapping("/get-user-dashboard")
    public ResponseEntity<Response<?>> getUserDashboard(Authentication authentication,
                                                        @RequestParam(name = "page", defaultValue = "0") int page,
                                                        @RequestParam(name = "size", defaultValue = "10") int size,
                                                        @RequestParam(name = "departmentId", required = false) Long departmentId,
                                                        @RequestParam(name = "textSearch", required = false) String textSearch ){
        return ResponseEntity.ok(Response.success(userService.getUserDashboard(authentication, page, size, departmentId,textSearch )));
    }

    @GetMapping("/get-infor-by-id")
    public ResponseEntity<Response<?>> getUserInformationById(@RequestParam(name = "userId") Long userId) {
        return ResponseEntity.ok(Response.success(userService.getUserInformationById(userId)));
    }

    @PutMapping("/update-user-by-admin")
    public ResponseEntity<Response<?>> updateUserByAdmin(@RequestBody UserInforRequest userInforRequest,
                                                         Authentication authentication) {
        return ResponseEntity.ok(
                Response.success(userService.updateUserByAdmin(userInforRequest, authentication))
        );
    }

    @DeleteMapping("/delete-user")
    public ResponseEntity<Response<?>> deleteUser(@RequestParam(name = "userId") Long userId,
                                                  Authentication authentication) {
        return ResponseEntity.ok(
                Response.success(userService.deleteUser(userId, authentication))
        );
    }
}
