package com.huinong.truffle.payment.order.mono.component.logger;

import java.util.Hashtable;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huinong.framework.autoconfigure.jackson.HnJson;
import com.huinong.framework.autoconfigure.web.BaseResult;
import com.huinong.framework.autoconfigure.web.ResultCode;


@Aspect
@Component
public class LoggerAspect {
  
    @Autowired
    private HnJson hnJson ; 
    
	ThreadLocal<Long> startTime = new ThreadLocal<Long>(); // 定义属性,解决同步问题
	static Hashtable<String,Logger> loggers = new Hashtable<String,Logger>();
	
	@Pointcut("execution(public * com.huinong.truffle.payment.order.mono.service.*.*(..))")
	public void Log()
	{
		
	}
	
	private  Logger getLogger(Class<?> clazz){
		String className = clazz.getCanonicalName();
		if(loggers.containsKey(className)){
			return loggers.get(className);
		}else{
			 Logger targetLogger  = org.slf4j.LoggerFactory.getLogger(clazz);
			loggers.put(className, targetLogger);
			return targetLogger;
		}
	}
 

	
	@Around("Log()")
	public Object around(ProceedingJoinPoint pjp) throws Throwable
	{
		String method = pjp.getSignature().getName();
		Class<?> clazz = pjp.getTarget().getClass();
		Logger logger = getLogger(clazz);
		Object [] parameters = pjp.getArgs() == null ? new Object[]{}: pjp.getArgs();
		logger.info(clazz.getName()  +"."+method +" start invoke. parameters :" + hnJson.obj2string(parameters));
		Object result = null;
		try {
			result = pjp.proceed();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(clazz.getName()  +"."+method +" invoke error. message :" + e.getMessage());
			throw e; 
		}finally{
			if(result != null){
				if(result instanceof BaseResult){
					BaseResult<?> baseResult = (BaseResult<?>)result;				
					if(ResultCode.SUCCESS.getCode() !=  baseResult.getCode()){
						logger.error(clazz.getName()  +"."+method +"("+hnJson.obj2string(parameters)+") invoke failure. result :" + hnJson.obj2string(result));
					}else{
						logger.info(clazz.getName() +"."+method +"("+hnJson.obj2string(parameters)+") invoke finish. result :" +  hnJson.obj2string(result) );
					}
				}else{
					logger.info(clazz.getName() +"."+method +"("+hnJson.obj2string(parameters)+")  invoke finish. result :" + hnJson.obj2string(result == null ? "null": result));
				}
			}
		}
		return result ;
	}

	
}
