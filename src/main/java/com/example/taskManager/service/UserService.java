package com.example.taskManager.service;

import com.example.taskManager.common.exception.CustomException;
import com.example.taskManager.common.exception.ResponseCode;
import com.example.taskManager.model.DTO.request.ChangePasswordRequest;
import com.example.taskManager.model.DTO.request.UserInforRequest;
import com.example.taskManager.model.DTO.response.UserDashBoard;
import com.example.taskManager.model.DTO.response.UserDashboardResponse;
import com.example.taskManager.model.DTO.response.UserDepartmnetResponse;
import com.example.taskManager.model.DTO.response.UserDetailDashBoard;
import com.example.taskManager.model.entity.Department;
import com.example.taskManager.model.entity.DepartmentUser;
import com.example.taskManager.model.entity.User;
import com.example.taskManager.repository.DepartmentRepository;
import com.example.taskManager.repository.DepartmentUserRepository;
import com.example.taskManager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final DepartmentUserRepository departmentUserRepository;

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


    public Page<UserDepartmnetResponse> getAllUsers(int page, int size, Long departmentId) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            var result = userRepository.findAllUser(departmentId,pageable);

            var response =  result.stream()
                    .map(res ->{

                        DepartmentUser departmentUser = departmentUserRepository.findByUserIdAndDepartmentId(res.getId(),departmentId);

                        var newRole = departmentUser != null ? departmentUser.getRole() : "";
                        if(newRole.equals("LEADER")){
                            newRole = "Lãnh đạo";
                            log.info("hahah");
                        }else{
                            newRole = "Thành viên";
                        }

                        UserDepartmnetResponse departmnetResponse = new UserDepartmnetResponse();
                        departmnetResponse.setId(res.getId());
                        departmnetResponse.setEmail(res.getEmail());
                        departmnetResponse.setFullName(res.getFullName());
                        departmnetResponse.setRole(newRole);

                        return departmnetResponse;
                    }).toList();

            return new PageImpl<>(response, pageable, result.getTotalElements());


        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve users: " + e.getMessage());
        }
    }



    @Transactional
    public UserDashboardResponse getUserDashboard(Authentication authentication, int page, int size) {
        try {
            List<User> admins = userRepository.findAdmin();
            List<User> leaders = userRepository.findLeaderDepartment();
            List<User> projectManagers = userRepository.findPM();

            Set<Long> specialIds = Stream.of(admins, leaders, projectManagers)
                    .flatMap(List::stream)
                    .map(User::getId)
                    .collect(Collectors.toSet());

            Pageable pageable = PageRequest.of(page, size);
            Page<User> memberPage = userRepository.findAllExcludeIds(specialIds, pageable);

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
            dashboard.setMembers(memberSet); // phần cần phân trang

            return new UserDashboardResponse(
                    dashboard,
                    memberPage.getTotalElements(),
                    memberPage.getTotalPages(),
                    memberPage.getNumber()
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to get dashboard", e);
        }
    }


}
