package com.oct.test;

import com.oct.account.AccountService;
import com.oct.user.UserDetailService;
import com.oct.user.UserService;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class SpringComponentScanTest {
    @Test
    public void test(){

        //1.基于注解创建容器
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext("config");
        //2.根据id获取bean
        UserService userService = context.getBean("userService", UserService.class);
        userService.save();
        UserDetailService userDetailService = context.getBean("userDetailService", UserDetailService.class);
        userDetailService.detail();
        AccountService accountService = context.getBean("accountService", AccountService.class);
        accountService.save();
    }
}
