package com.colin.secondkill.service.impl;

import com.colin.secondkill.bean.Admin;
import com.colin.secondkill.mapper.AdminMapper;
import com.colin.secondkill.service.AdminService;
import com.colin.secondkill.util.EncipherUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 2024年07月18日上午9:48
 */
@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminMapper adminMapper;
    @Autowired
    private EncipherUtil encipherUtil;

    @Override
    public Admin doLogin(String adminName, String password) {
        final String finalPassword = encipherUtil.doEncipher(password);
        return adminMapper.selectUserByUsernameAndPassword(adminName, finalPassword);
    }
}
