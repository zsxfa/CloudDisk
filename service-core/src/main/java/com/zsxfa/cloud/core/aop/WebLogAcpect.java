package com.zsxfa.cloud.core.aop;

import com.zsxfa.cloud.base.util.JwtUtils;
import com.zsxfa.cloud.base.util.SessionUtil;
import com.zsxfa.cloud.core.config.OperationLogUtil;
import com.zsxfa.cloud.core.pojo.entity.User;
import com.zsxfa.cloud.core.service.OperationlogService;
import com.zsxfa.cloud.core.service.UserService;
import com.zsxfa.common.result.R;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.CodeSignature;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 操作日志切面
 */
@Aspect
@Component
public class WebLogAcpect {
    @Resource
    OperationlogService operationLogService;
    @Resource
    UserService userService;

    private String operation = "";
    private String module = "";
    private String token = "";
    private HttpServletRequest request;


    /**
     * 定义切入点，切入点为com.example.aop下的所有函数
     */
    @Pointcut("@annotation(com.zsxfa.cloud.core.aop.MyLog)")
//    @Pointcut("execution(public * com.zsxfa.cloud.core.controller.*.*(..))")
    public void webLog() {
    }

    /**
     * 前置通知：在连接点之前执行的通知
     *
     * @param joinPoint 切入点
     */
    @Before("webLog()")
    public void doBefore(JoinPoint joinPoint) {
        //从切面织入点处通过反射机制获取织入点处的方法
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        //获取切入点所在的方法
        Method method = signature.getMethod();
        Map<String, Object> map = getNameAndValue(joinPoint);

        //获取操作
        MyLog myLog = method.getAnnotation(MyLog.class);

        if (myLog != null) {
            operation = myLog.operation();
            module = myLog.module();
            token = (String) map.get("token");
        }

        // 接收到请求，记录请求内容
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        request = attributes.getRequest();


    }

    @AfterReturning(returning = "ret", pointcut = "webLog()")
    public void doAfterReturning(Object ret) throws Throwable {

        if (ret instanceof R) {
            boolean isSuccess = ((R) ret).getSuccess();
            String errorMessage = ((R) ret).getMessage();

            String token = request.getHeader("token");
            if(null != token){
                System.out.println("token是："+token);
                System.out.println(request.getRequestURI());
                System.out.println(request.getRemoteAddr());
                User sessionUserBean = userService.getUserByToken(token);
                System.out.println("sessionUserBean是："+sessionUserBean);
                if (isSuccess) {
                    operationLogService.insertOperationLog(
                            OperationLogUtil.getOperationLogObj(request,sessionUserBean, "成功", module, operation, "操作成功"));
                } else {
                    operationLogService.insertOperationLog(
                            OperationLogUtil.getOperationLogObj(request,sessionUserBean, "失败", module, operation, errorMessage));
                }
            }



        }

    }

    /**
     * 获取参数Map集合
     * @param joinPoint
     * @return
     */
    Map<String, Object> getNameAndValue(JoinPoint joinPoint) {
        Map<String, Object> param = new HashMap<>();
        Object[] paramValues = joinPoint.getArgs();
        String[] paramNames = ((CodeSignature)joinPoint.getSignature()).getParameterNames();
        for (int i = 0; i < paramNames.length; i++) {
            param.put(paramNames[i], paramValues[i]);
        }
        return param;
    }
}