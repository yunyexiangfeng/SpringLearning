package config;

import com.oct.user.UserService;
import com.oct.user.impl.UserServiceImpl;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

/**
 * spring配置类，相当于applicationContext.xml
 */
@Configuration(value = "springConfiguration")
//不指定属性值时，扫描当前类所在基础包（即config）下的所有类
//@ComponentScan()
//@ComponentScan(value = {"com.oct"})
//表示扫描UserServiceImpl所在基础包（即com.oct.user）下的所有类
@ComponentScan(basePackageClasses = UserService.class,
        resourcePattern = "**/*.class",
        includeFilters = @ComponentScan.Filter(value = Service.class),
        excludeFilters = @ComponentScan.Filter(value = Controller.class))
public class SpringConfiguration {
}
