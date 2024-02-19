package com.oct.user.impl;

import com.oct.user.UserService;
import org.springframework.stereotype.Service;

@Service(value = "userService")
public class UserServiceImpl implements UserService {
    public void save() {
        System.out.println("save user!");
    }
}
