package TWAJavaTraining.ExcelTraining;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Before("execution(* TWAJavaTraining.ExcelTraining.Service.*.*(..)) || execution(* TWAJavaTraining.ExcelTraining.Controllers.*.*(..))")
    public void logBefore(JoinPoint joinPoint) {
        logger.info("Entering method: {} in class: {}", joinPoint.getSignature().getName(),
                joinPoint.getSignature().getDeclaringTypeName());
    }

    @AfterReturning(pointcut = "execution(* TWAJavaTraining.ExcelTraining.Service.*.*(..)) || execution(* TWAJavaTraining.ExcelTraining.Controllers.*.*(..))" , returning = "result")
    public void logAfter(JoinPoint joinPoint, Object result) {
        logger.info("Exiting method: {} in class: {} with result: {}",
                joinPoint.getSignature().getName(), joinPoint.getSignature().getDeclaringTypeName(),
                result);
    }

}
