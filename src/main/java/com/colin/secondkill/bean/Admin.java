package com.colin.secondkill.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 2024年07月13日下午7:00
 * 管理员
 * 暂时只管商品的增删改查
 * 用户名：admin
 * 密码：admin
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Admin {
    private Integer id;
    private String userName;
    private String password;
}
