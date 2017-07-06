package com.huinong.truffle.payment.order.mono.component.logger;

import java.util.Hashtable;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.huinong.truffle.component.base.constants.BaseResult;
import com.huinong.truffle.component.base.constants.ResultCode;


@Aspect
@Component
public class LoggerAspect {
	
	ThreadLocal<Long> startTime = new ThreadLocal<Long>(); // 定义属性,解决同步问题
	static Gson gson = new Gson();
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
		Object [] parameters = pjp.getArgs();
		logger.warn(clazz.getName()  +"."+method +" start invoke. parameters :" + gson.toJson(parameters == null ? new Object[]{}:parameters));
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
					BaseResult baseResult = (BaseResult)result;				
					if(ResultCode.SUCCESS.getCode() !=  baseResult.getCode()){
						logger.error(clazz.getName()  +"."+method +"("+gson.toJson(result)+") invoke failure. result :" + gson.toJson(result));
					}else{
						logger.warn(clazz.getName() +"."+method +"("+gson.toJson(result)+") invoke finish. result :" +  gson.toJson(result) );
					}
				}else{
					logger.warn(clazz.getName() +"."+method +"("+gson.toJson(result)+")  invoke finish. result :" + gson.toJson(result == null ? "null": result));
				}
			}
		}
		return result ;
	}

	
}
