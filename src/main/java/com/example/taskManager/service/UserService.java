package com.example.taskManager.service;

import com.example.taskManager.common.exception.CustomException;
import com.example.taskManager.common.exception.ResponseCode;
import com.example.taskManager.model.DTO.request.ChangePasswordRequest;
import com.example.taskManager.model.DTO.request.UserInforRequest;
import com.example.taskManager.model.DTO.response.UserDashBoard;
import com.example.taskManager.model.DTO.response.UserDetailDashBoard;
import com.example.taskManager.model.entity.Department;
import com.example.taskManager.model.entity.User;
import com.example.taskManager.repository.DepartmentRepository;
import com.example.taskManager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final DepartmentRepository departmentRepository;

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

    public Page<User> getAllUsers(int page, int size, Long departmentId) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            return userRepository.findAllUser(departmentId,pageable);
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve users: " + e.getMessage());
        }
    }


    @Transactional
    public UserDashBoard getUserDashboard(Authentication authentication, int page, int size) {
        try {
            // Các nhóm đặc biệt không phân trang
            List<User> admins = userRepository.findAdmin();
            List<User> leaders = userRepository.findLeaderDepartment();
            List<User> projectManagers = userRepository.findPM();

            // Lấy danh sách ID đã có vai trò đặc biệt
            Set<Long> specialIds = Stream.of(admins, leaders, projectManagers)
                    .flatMap(List::stream)
                    .map(User::getId)
                    .collect(Collectors.toSet());

            // Phân trang cho thành viên (không thuộc các vai trò trên)
            Pageable pageable = PageRequest.of(page, size);
            Page<User> memberPage = userRepository.findAllExcludeIds(specialIds, pageable);

            // Map từng nhóm sang DTO
            Set<UserDetailDashBoard> adminSet = admins.stream()
                    .map(u -> new UserDetailDashBoard(u.getId(), u.getFullName(), null))
                    .collect(Collectors.toSet());

            Set<UserDetailDashBoard> leaderSet = leaders.stream()
                    .map(u -> new UserDetailDashBoard(u.getId(), u.getFullName(), null))
                    .collect(Collectors.toSet());

            Set<UserDetailDashBoard> pmSet = projectManagers.stream()
                    .map(u -> new UserDetailDashBoard(u.getId(), u.getFullName(), null))
                    .collect(Collectors.toSet());

            Set<UserDetailDashBoard> memberSet = memberPage.getContent().stream()
                    .map(u -> new UserDetailDashBoard(u.getId(), u.getFullName(), null))
                    .collect(Collectors.toSet());

            UserDashBoard dashboard = new UserDashBoard();
            dashboard.setAdmins(adminSet);
            dashboard.setLeaderDepartments(leaderSet);
            dashboard.setProjectManagers(pmSet);
            dashboard.setMembers(memberSet); // phân trang

            return dashboard;
        } catch (Exception e) {
            throw new RuntimeException("Failed to get dashboard", e);
        }
    }

}
