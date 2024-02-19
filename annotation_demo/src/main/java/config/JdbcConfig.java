package config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

/**
 * JDBC相关的配置类
 */
public class JdbcConfig {

    @Value("${jdbc.driver}")
    public String driver;
    @Value("${jdbc.url}")
    public String url;
    @Value("${jdbc.username}")
    public String username;
    @Value("${jdbc.password}")
    public String password;

    /**
     * 创建JdbcTemplate对象，并存入ioc容器中
     *Autowired 按类型注入，此注解可以不加，spring会自动传参
     * @return
     */
    //指定bean id。此时jdbcTemplate bean为非单例
    @Bean(value = "jdbcTemplate")
    public JdbcTemplate createJdbcTemplate(@Autowired DataSource dataSource){
        return new JdbcTemplate(dataSource);
    }

    /**
     * 创建数据源，并存入ioc容器
     * @return
     */
    @Bean
    public DataSource createDataSource(){
        //1.创建spring内置数据源对象
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        //2.给数据源提供必要参数
        dataSource.setDriverClassName(driver);
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        //3.返回
        return dataSource;
    }
}
