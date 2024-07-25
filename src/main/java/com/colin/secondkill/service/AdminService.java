package com.colin.secondkill.service;

import com.colin.secondkill.bean.Admin;

/**
 * 2024年07月18日上午9:47
 */
public interface AdminService {

    Admin doLogin(String adminName, String password);
}
