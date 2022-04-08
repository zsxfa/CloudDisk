package com.zsxfa.cloud.core;

import com.zsxfa.cloud.core.config.fileConf.autoconfiguration.UFOPAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

/**
 * @author zsxfa
 */
@SpringBootApplication
@ComponentScan({"com.zsxfa.cloud.*","com.zsxfa.cloud.base","com.zsxfa.common"})
public class ServiceCoreApplication {

    public static void main(String[] args) {
        try {
            SpringApplication.run(ServiceCoreApplication.class, args);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
