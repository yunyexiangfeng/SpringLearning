package com.oct.test;

import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;

/**
 * spring驱动注解开发入门 测试
 */
public class SpringAnnotationTest {

    @Test
    public void test(){

        //1.创建容器 （基于注解）
        //basePackages就是@Configuration注解所在的package
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext("config");
        //2.根据bean id获取对象
        JdbcTemplate jdbcTemplate = context.getBean("jdbcTemplate", JdbcTemplate.class);

        //3.执行操作
        List<Map<String, Object>> maps = jdbcTemplate.queryForList("select * from city;");
        maps.forEach(map -> {
            System.out.println(map.toString());
        });
    }
}
