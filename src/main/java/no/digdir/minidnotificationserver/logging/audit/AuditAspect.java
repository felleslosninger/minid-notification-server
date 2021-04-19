package no.digdir.minidnotificationserver.logging.audit;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Aspect
@Component
@RequiredArgsConstructor
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class AuditAspect {

    private final AuditService auditService;

    @Around("@annotation(audit)")
    public Object auditAnnotatedFunctions(ProceedingJoinPoint joinPoint, Audit audit) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        AuditID auditId = audit.auditId();

        Map<String, Object> parameterMap = new HashMap<>();
        String[] parameterNames = signature.getParameterNames(); // method parameter names
        Object[] parameterValues = joinPoint.getArgs(); // method parameter values

        for(int i = 0; i < parameterNames.length; i++) {
            String parameterName = convertToSnakeCase(parameterNames[i]);
            Object parameterValue = parameterValues[i];
            parameterMap.put(parameterName, parameterValue);
        }

        Object retval = null;
        boolean success = false;
        try {
            retval = joinPoint.proceed();
            success = true;

        } catch (Exception e) {
            parameterMap.put("exception", e.getMessage());
            throw e;
        } finally {
            if(retval != null) {
                parameterMap.put("result", retval);
            }
            parameterMap.put("success", success);
            auditService.audit(auditId, parameterMap);
        }
        return retval;
    }

    private String convertToSnakeCase(String str) {
        return str.replaceAll("([a-z])([A-Z]+)", "$1_$2").toLowerCase();
    }

}
