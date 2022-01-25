package com.zsxfa.cloud.base.config;

import com.google.common.base.Predicates;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author zsxfa
 */
@Configuration
@EnableSwagger2
public class Swagger2Config {

    @Bean
    public Docket adminApiConfig(){

        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("adminApi")
                .apiInfo(adminApiInfo())
                .select()
                //只显示admin路径下的页面
                .paths(Predicates.and(PathSelectors.regex("/admin/.*")))
                .build();
    }

    private ApiInfo adminApiInfo(){

        return new ApiInfoBuilder()
                .title("Hadoop网络云盘后台管理系统-API文档")
                .description("本文档描述了Hadoop网络云盘管理员后台系统接口")
                .version("1.0")
                .contact(new Contact("Jachin", "http://zsxfa.cn", "zsxfa125689@163.com"))
                .build();
    }

    @Bean
    public Docket webApipConfig(){
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("webApi")
                .apiInfo(webApiInfo())
                .select()
                .paths(Predicates.and(PathSelectors.regex("/api/.*")))
                .build();
    }

    private ApiInfo webApiInfo(){
        return new ApiInfoBuilder()
                .title("Hadoop网络云盘后台管理系统-API文档")
                .description("本文档描述了Hadoop网络云盘用户后台系统接口")
                .version("1.0")
                .contact(new Contact("Jachin", "http://zsxfa.cn", "zsxfa125689@163.com"))
                .build();
    }
}
