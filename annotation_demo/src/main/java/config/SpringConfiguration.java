package config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

/**
 * spring配置类，相当于applicationContext.xml
 */
@Configuration
//指定配置文件
@PropertySource(value = {"classpath:jdbc.properties"})
//导入java配置
@Import(value = {JdbcConfig.class})
public class SpringConfiguration {
}
