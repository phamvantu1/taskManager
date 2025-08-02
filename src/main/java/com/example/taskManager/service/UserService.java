package com.example.taskManager.service;

import com.example.taskManager.common.exception.CustomException;
import com.example.taskManager.common.exception.ResponseCode;
import com.example.taskManager.model.DTO.request.ChangePasswordRequest;
import com.example.taskManager.model.DTO.request.UserInforRequest;
import com.example.taskManager.model.DTO.response.*;
import com.example.taskManager.model.entity.Department;
import com.example.taskManager.model.entity.DepartmentUser;
import com.example.taskManager.model.entity.Role;
import com.example.taskManager.model.entity.User;
import com.example.taskManager.repository.DepartmentRepository;
import com.example.taskManager.repository.DepartmentUserRepository;
import com.example.taskManager.repository.RoleRepository;
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
import org.springframework.util.StringUtils;

import java.time.LocalDate;
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
    private final DepartmentRepository departmentRepository;
    private final RoleRepository roleRepository;

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
    public UserDashboardResponse getUserDashboard(Authentication authentication, int page, int size, Long departmentId, String textSearch) {
        try {
            List<User> admins = userRepository.findAdmin(departmentId,textSearch);
            List<User> leaders = userRepository.findLeaderDepartment(departmentId,textSearch);
            List<User> projectManagers = userRepository.findPM(departmentId,textSearch);

            Set<Long> specialIds = Stream.of(admins, leaders, projectManagers)
                    .flatMap(List::stream)
                    .map(User::getId)
                    .collect(Collectors.toSet());

            Pageable pageable = PageRequest.of(page, size);
            Page<User> memberPage = userRepository.findAllExcludeIds(specialIds,departmentId,textSearch, pageable);


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

    public UserInfoResponse getUserInformationById(Long userId) {
        try {

            UserInfoResponse userInfoResponse = new UserInfoResponse();

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new CustomException(ResponseCode.USER_NOT_FOUND));

            DepartmentUser departmentUser = departmentUserRepository.findFirstByUserId(userId);

            if (departmentUser != null){
                departmentRepository.findById(departmentUser.getDepartment().getId())
                        .ifPresent(department -> userInfoResponse.setDepartmentName(department.getName()));
            }

            Role role = user.getRoles().stream()
                    .findFirst().orElse(null);

            userInfoResponse.setId(user.getId());
            userInfoResponse.setFirstName(user.getFirstName());
            userInfoResponse.setLastName(user.getLastName());
            userInfoResponse.setDateOfBirth( user.getDateOfBirth() != null ? user.getDateOfBirth().toString() : null);
            userInfoResponse.setGender(user.getGender());
            userInfoResponse.setPhone(user.getPhone());
            userInfoResponse.setRole(role != null ? role.getName() : null);
            userInfoResponse.setEmail(user.getEmail());

            return userInfoResponse;

        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve user information: " + e.getMessage());
        }
    }


    @Transactional
    public Map<String, String> updateUserByAdmin(UserInforRequest userInforRequest, Authentication authentication) {
        try {

            User user = userRepository.findById(userInforRequest.getId())
                    .orElseThrow(() -> new CustomException(ResponseCode.USER_NOT_FOUND));

            user.setFirstName(userInforRequest.getFirstName());
            user.setLastName(userInforRequest.getLastName());
            user.setPhone(userInforRequest.getPhone());
            user.setDateOfBirth(userInforRequest.getDateOfBirth());
            user.setGender(userInforRequest.getGender());

            if (StringUtils.hasText(userInforRequest.getRole())){
                Role role = roleRepository.findFirstByName(userInforRequest.getRole())
                        .orElseThrow(() -> new CustomException(ResponseCode.ROLE_NOT_FOUND));
                Set<Role> roles = new HashSet<>(); // Tạo một HashSet có thể thay đổi
                roles.add(role);
                user.setRoles(roles);
            }

            userRepository.save(user);

            return Map.of("message", "Cập nhập thông tin người dùng thành công");
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to update user information: " + e.getMessage());
        }
    }

    @Transactional
    public Map<String ,String > deleteUser(Long userId, Authentication authentication) {
        try {

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new CustomException(ResponseCode.USER_NOT_FOUND));

            user.setIsActive(false);
            userRepository.save(user);

            return Map.of("message", "Xoá người dùng thành công");
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete user: " + e.getMessage());
        }
    }

}
