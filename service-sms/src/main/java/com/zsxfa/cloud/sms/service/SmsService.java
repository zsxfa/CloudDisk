package com.zsxfa.cloud.sms.service;

import java.util.Map;

/**
 * @author zsxfa
 */
public interface SmsService {

    void send(String mobile, String templateCode, Map<String,Object> param);
}
