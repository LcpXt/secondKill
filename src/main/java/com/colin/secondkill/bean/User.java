package com.colin.secondkill.bean;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.sql.Date;
import java.sql.Timestamp;

/**
 * {@code @Info}
 *
 * @author 777
 * {@code @date} 2024-03-26
 * {@code @time} 15:51
 */
@Data
@NoArgsConstructor
public class User {

    // hello
    /**
     * 用户主键id
     */
    private Integer id;

    /**
     * 用户名
     */
    @Pattern(regexp = "^[a-zA-Z0-9]{3,12}$", message = "用户名不符合规则")
    private String username;

    /**
     * 密码
     */
    // @Pattern(regexp = "^.*(?=.{6,})(?=.*\\d)(?=.*[A-Z])(?=.*[a-z])(?=.*[!@#$%^&*? ]).*$", message = "密码不符合规则")
    private String password;

    /**
     * 邮件
     */
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "邮箱格式不符合规则")
    private String email;

    /**
     * 注册时间
     */
    private Timestamp registerTime;

    /**
     * 更新时间
     */
    private Timestamp updateTime;

    /**
     * 最后一次登录时间
     */
    private Timestamp lastLoginTime;

    /**
     * 用户类别
     * 0: 普通用户
     * 1: VIP用户
     * 2: SVIP用户
     * 3: 创作者
     * 4: VIP + 创作者
     * 5: SVIP + 创作者
     */
    private Integer userType;

    /**
     * 年龄
     */
    @Min(value = 18, message = "年龄最小值不能小于18")
    @Max(value = 60, message = "年龄最大值不能大于60")
    private Integer age;

    /**
     * 性别
     */
    @NotNull
    private String sex;

    /**
     * VIP / SVIP 过期时间
     */
    private Date expirationDate;

    /**
     * 头像对象
     */
    private String headImg;

    /**
     * 电话号码
     */
    private String phoneNumber;

    /**
     * 个性签名
     */
    private String description;


    private User(UserBuilder userBuilder) {
        this.id = userBuilder.id;
        this.username = userBuilder.username;
        this.password = userBuilder.password;
        this.email = userBuilder.email;
        this.registerTime = userBuilder.registerTime;
        this.updateTime = userBuilder.updateTime;
        this.lastLoginTime = userBuilder.lastLoginTime;
        this.userType = userBuilder.userType;
        this.age = userBuilder.age;
        this.sex = userBuilder.sex;
        this.expirationDate = userBuilder.expirationDate;
        this.headImg = userBuilder.headImg;
        this.phoneNumber = userBuilder.phoneNumber;
        this.description = userBuilder.description;
    }

    public static class UserBuilder {
        private Integer id;
        private String username;
        private String password;
        private String email;
        private Timestamp registerTime;
        private Timestamp updateTime;
        private Timestamp lastLoginTime;
        private Integer userType;
        private Integer age;
        private String sex;
        private Date expirationDate;
        private String headImg;
        private String phoneNumber;
        private String description;
        private String salt;

        public UserBuilder id(Integer id) {
            this.id = id;
            return this;
        }

        public UserBuilder username(String username) {
            this.username = username;
            return this;
        }

        public UserBuilder password(String password) {
            this.password = password;
            return this;
        }

        public UserBuilder email(String email) {
            this.email = email;
            return this;
        }

        public UserBuilder registerTime(Timestamp registerTime) {
            this.registerTime = registerTime;
            return this;
        }

        public UserBuilder updateTime(Timestamp updateTime) {
            this.updateTime = updateTime;
            return this;
        }

        public UserBuilder lastLoginTime(Timestamp lastLoginTime) {
            this.lastLoginTime = lastLoginTime;
            return this;
        }

        public UserBuilder userType(Integer userType) {
            this.userType = userType;
            return this;
        }

        public UserBuilder age(Integer age) {
            this.age = age;
            return this;
        }

        public UserBuilder sex(String sex) {
            this.sex = sex;
            return this;
        }

        public UserBuilder expirationDate(Date expirationDate) {
            this.expirationDate = expirationDate;
            return this;
        }

        public UserBuilder headImg(String headImg) {
            this.headImg = headImg;
            return this;
        }

        public UserBuilder phoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public UserBuilder description(String description) {
            this.description = description;
            return this;
        }

        public User build() {
            return new User(this);
        }


    }
}
