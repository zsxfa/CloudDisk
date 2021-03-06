package com.zsxfa.cloud.core.config;


import com.zsxfa.cloud.core.interceptor.AuthenticationInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
	@Autowired
	private AuthenticationInterceptor authenticationInterceptor;

	/**
	 * app拦截器
	 */
	@Override
	public void addInterceptors(InterceptorRegistry registry) {

		List<String> list = new ArrayList<>();
		list.add("/api/core/file/**");
		list.add("/api/core/hdfs/**");
		list.add("/api/core/recoveryfile/**");
//		list.add("/api/core/user/**");
		registry.addInterceptor(authenticationInterceptor)
			.addPathPatterns(list)
			.excludePathPatterns("/api/core/user/checkuserlogininfo",
					"/api/core/hdfs/downloadfile",
					"/api/core/hdfs/preview");
	}

}