package com.example.taskManager.repository;

import com.example.taskManager.model.entity.UserOTP;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.validation.annotation.Validated;

public interface OtpRepository extends JpaRepository<UserOTP, Long> {

    @Query(value = "SELECT u.* FROM user_otp u WHERE " +
            " u.user_id = :userId  " +
            " AND u.otp_code = :otp",
             nativeQuery = true)
    UserOTP findByUserIdAndOtp(@Param("userId") Long userId, @Param("otp") String otp);

}
