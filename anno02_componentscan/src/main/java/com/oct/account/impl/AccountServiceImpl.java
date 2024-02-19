package com.oct.account.impl;

import com.oct.account.AccountService;
import org.springframework.stereotype.Service;

@Service(value = "accountService")
public class AccountServiceImpl implements AccountService {
    public void save() {
        System.out.println("save account!");
    }
}
