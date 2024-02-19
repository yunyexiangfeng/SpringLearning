package com.oct.user.impl;

import com.oct.user.UserDetailService;
import org.springframework.stereotype.Service;

@Service(value = "userDetailService")
public class UserDetailServiceImpl implements UserDetailService {
    public void detail() {
        System.out.println("see detail of user!");
    }
}
