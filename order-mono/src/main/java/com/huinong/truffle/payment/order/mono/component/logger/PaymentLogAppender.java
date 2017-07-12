package com.huinong.truffle.payment.order.mono.component.logger;

import java.io.Serializable;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.appender.AppenderLoggingException;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;

import com.alibaba.fastjson.JSON;
import com.huinong.truffle.component.base.component.redis.service.RedisService;
import com.huinong.truffle.payment.order.mono.constant.OrderConstants;
import com.huinong.truffle.payment.order.mono.util.SpringBeanUtils;


/**
 * 将日志信息存入redis
 * @author Administrator
 *
 */
@Plugin(name = "PaymentLogAppender", category = "Core", elementType = "appender", printObject = true)
public class PaymentLogAppender extends AbstractAppender {
	
	
	private RedisService redis = null ;
	
    private final ReadWriteLock rwLock = new ReentrantReadWriteLock();
    
    private final Lock readLock = rwLock.readLock();
    
    protected PaymentLogAppender(final String name, final Filter filter, final Layout<? extends Serializable> layout,
            final boolean ignoreExceptions) {
        super(name, filter, layout, ignoreExceptions);
    }
	
    
    
	@Override
	public void append(LogEvent event) {
		 readLock.lock();
	        try {
	            if(redis == null){
	            	try {
						Collection<RedisService>  beans = SpringBeanUtils.getBeans(RedisService.class);
						if(beans== null || beans.size() == 0) return ;
						redis = beans.iterator().next();
					} catch (Exception e) {
					}
	            }
	            if(redis == null) return ;
	            LogInfo logInfo=new LogInfo();
	            //日志时间
	            String logTime=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(event.getTimeMillis()));
	            //报错类
	            String logFullClass=event.getLoggerName();
	            //日志级别
	            String logLevel=event.getLevel().name();
	            //日志信息
	            String message=event.getMessage().getFormattedMessage();
	            logInfo.setLogFullClass(logFullClass);
	            
	            if(!logFullClass.contains("com.huinong.truffle.payment")) return ;
	            logInfo.setLogTime(logTime);
	            logInfo.setLogLevel(logLevel);
	            logInfo.setMessage(message);
	            redis.rpush(OrderConstants.Payment_Log.PAYMENT_LOG_ERROR.val, JSON.toJSON(logInfo));
	        } catch (Exception ex) {
	            if (!ignoreExceptions()) {
	                throw new AppenderLoggingException(ex);
	            }
	        } finally {
	            readLock.unlock();
	        }
	}
	
	@PluginFactory
    public static PaymentLogAppender createAppender(@PluginAttribute("name") String name,
            @PluginElement("Filter") final Filter filter,
            @PluginElement("Layout") Layout<? extends Serializable> layout,
            @PluginAttribute("ignoreExceptions") boolean ignoreExceptions) {
        if (name == null) {
            LOGGER.error("No name provided for MyCustomAppenderImpl");
            return null;
        }
        if (layout == null) {
            layout = PatternLayout.createDefaultLayout();
        }
        return new PaymentLogAppender(name, filter, layout, ignoreExceptions);
    }


}
