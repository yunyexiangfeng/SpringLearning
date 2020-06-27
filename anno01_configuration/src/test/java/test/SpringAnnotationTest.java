package test;

import config.SpringConfiguration;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * spring驱动注解开发入门 测试
 */
public class SpringAnnotationTest {

    @Test
    public void test(){
        //扫描包的方式 SpringConfiguration使用@Configuration注解
        //1.创建容器 （基于注解）
        //basePackages就是@Configuration注解所在的package
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext("config");

        //2.通过id获取bean
        // 可直接获取bean
        /*SpringConfiguration springConfiguration = context.getBean("springConfiguration", SpringConfiguration.class);
        System.out.println(springConfiguration);*/


        //2.当bean未指定id
        // 可通过类型获取bean
        // 此时会通过CGLIB动态代理获取bean
        SpringConfiguration springConfiguration = context.getBean(SpringConfiguration.class);
        System.out.println(springConfiguration);



        //传入类型（字节码）方式 SpringConfiguration使用@Component注解
        //1.创建容器 （基于注解）
        AnnotationConfigApplicationContext context2 =
                new AnnotationConfigApplicationContext(SpringConfiguration.class);
        //2.通过类型获取bean
        // 此时会通过CGLIB动态代理获取bean
        System.out.println(context2.getBean(SpringConfiguration.class));
    }
}
