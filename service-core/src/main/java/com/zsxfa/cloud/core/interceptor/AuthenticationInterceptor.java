package com.zsxfa.cloud.core.interceptor;

import com.zsxfa.cloud.base.util.SessionUtil;
import com.zsxfa.cloud.core.pojo.entity.User;
import com.zsxfa.cloud.core.service.UserService;
import com.zsxfa.common.exception.DefinedException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * token验证拦截
 */
@Slf4j
@Component
public class AuthenticationInterceptor implements HandlerInterceptor {

    @Resource
    private UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 取得token
//        String token = request.getHeader("token");//HttpHeaders.AUTHORIZATION
//        if("undefined".equals(token) || StringUtils.isEmpty(token)){
//            token=request.getParameter("token不存在");
//        }
//        if ("undefined".equals(token) || StringUtils.isEmpty(token)) {
//            throw new DefinedException("token不存在");
//        }
//        User userBean = userService.getUserByToken(token);
//        SessionUtil.setSession(userBean);
//        if (userBean == null) {
//            throw new DefinedException();
//        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        // TODO Auto-generated method stub

    }

}
