package com.zsxfa.cloud.sms.client;

import com.zsxfa.cloud.sms.client.fallback.CoreUserClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "service-core", fallback = CoreUserClientFallback.class)
public interface CoreUserClient {

    @GetMapping("/api/core/user/checkMobile/{mobile}")
    boolean checkMobile(@PathVariable String mobile);
}
