package com.example.taskManager.service;

import com.example.taskManager.common.exception.CustomException;
import com.example.taskManager.common.exception.ResponseCode;
import com.example.taskManager.model.DTO.request.ChangePasswordRequest;
import com.example.taskManager.model.DTO.request.UserInforRequest;
import com.example.taskManager.model.entity.User;
import com.example.taskManager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User getUserByEmail(Authentication authentication) {
        try{
            return userRepository.findByEmail(authentication.getName())
                    .orElseThrow(() -> new CustomException(ResponseCode.USER_NOT_FOUND));
        }catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve user: " + e.getMessage());
        }
    }

    public Map<String, String> changePassword(ChangePasswordRequest changePasswordRequest, Authentication authentication) {

        try{
            String email  = authentication.getName();
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new CustomException(ResponseCode.USER_NOT_FOUND));

            if (!userRepository.existsByEmail(user.getEmail())) {
                throw new CustomException(ResponseCode.USER_NOT_FOUND);
            }

            if (!passwordEncoder.matches(changePasswordRequest.getOldPassword(), user.getPassword())) {
                throw new CustomException(ResponseCode.INVALID_OLD_PASSWORD);
            }

            if (!changePasswordRequest.getNewPassword().equals(changePasswordRequest.getConfirmNewPassword())) {
                throw new CustomException(ResponseCode.CONFIRM_PASSWORD_NOT_MATCH);
            }

            user.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
            userRepository.save(user);

            return Map.of("message", "Password changed successfully");
        }catch(CustomException e ){
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Password change failed: " + e.getMessage());
        }
    }

    public Map<String , String> updateUserInformation(UserInforRequest userInforRequest, Authentication authentication) {

       try{
           if (!userInforRequest.getEmail().equals(authentication.getName())){
               throw new CustomException(ResponseCode.USER_CAN_NOT_UPDATE);
           }

           User user = userRepository.findByEmail(authentication.getName())
                   .orElseThrow(() -> new CustomException(ResponseCode.USER_NOT_FOUND));

           user.setFirstName(userInforRequest.getFirstName());
           user.setLastName(userInforRequest.getLastName());
           user.setPhone(userInforRequest.getPhone());
           user.setDateOfBirth(userInforRequest.getDateOfBirth());
           user.setGender(userInforRequest.getGender());

           userRepository.save(user);

           return Map.of("message", "User information updated successfully");
       }catch(CustomException e) {
           throw e;
       } catch (Exception e) {
           throw new RuntimeException(e.getMessage());
       }
    }

    public Page<User> getAllUsers(int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            return userRepository.findAll(pageable);
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve users: " + e.getMessage());
        }
    }
}
