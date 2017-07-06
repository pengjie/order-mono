package com.huinong.truffle.payment.order.mono.util;

import java.util.Collection;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class SpringBeanUtils implements ApplicationContextAware{

	
	 private static ApplicationContext applicationContext;

	    @Override
	    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
	        if(SpringBeanUtils.applicationContext == null) {
	        	SpringBeanUtils.applicationContext = applicationContext;
	        }
	    }

	    //获取applicationContext
	    public static ApplicationContext getApplicationContext() {
	        return applicationContext;
	    }

	    //通过name获取 Bean.
	    public static Object getBean(String name){
	        return getApplicationContext().getBean(name);
	    }
	    
	    
	    public static <T> Collection<T> getBeans(Class<T> type){
	    	Map<String, T> beanMap =  applicationContext.getBeansOfType(type);
	    	if(beanMap != null) return beanMap.values();
	    	return null ;
	    }

}
