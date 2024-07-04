package com.colin.secondkill.mapper;

import com.colin.secondkill.bean.User;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;

/**
 * 2024年03月23日16:58
 */
@Repository
public interface UserMapper {

    Integer selectIdByUsername(String username);

    Boolean insertUser(User user);

    Boolean updatePasswordByEmail(String email, String finalPassword);

    User selectUserByUsernameAndPassword(String username, String finalPassword);

    void updateLastLoginTime(Timestamp currentTime, String username);

    void updateHeadImgById(Integer id, String mappingPath);

    void updateUserById(User user);

    User selectUserById(Integer id);
}
