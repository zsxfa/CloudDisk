package com.zsxfa.cloud.sms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author zsxfa
 */
@EnableFeignClients
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)//取消数据源自动配置
@ComponentScan({"com.zsxfa.cloud","com.zsxfa.common"})
public class ServiceSmsApplication {
    public static void main(String[] args) {
        SpringApplication.run(com.zsxfa.cloud.sms.ServiceSmsApplication.class, args);
    }
}
