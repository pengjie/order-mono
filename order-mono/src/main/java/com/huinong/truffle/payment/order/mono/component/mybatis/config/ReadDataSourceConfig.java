/**
 * Project Name:api-access
 * File Name:WriteDataSourceEntity.java
 * Package Name:com.huinong.gateway.access.config.datasource
 * Date:2017年5月22日下午6:47:44
 * Copyright (c) 2017, chenzhou1025@126.com All Rights Reserved.
 *
*/
package com.huinong.truffle.payment.order.mono.component.mybatis.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import com.huinong.truffle.component.base.component.mybatis.config.DataSourceConfig;

/**
 * ClassName:WriteDataSourceEntity <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2017年5月22日 下午6:47:44 <br/>
 * @author lch
 * @version
 * @since JDK 1.6
 * @see
 */
@Configuration
// @RefreshScope 不支持自动刷新
public class ReadDataSourceConfig implements DataSourceConfig
{
	@Value("${user.read.datasource.url}")
	private String url;
	@Value("${user.read.datasource.username}")
	private String user;
	@Value("${user.read.datasource.password}")
	private String password;
	@Value("${user.read.datasource.driverClassName}")
	private String driverClass;
	@Value("${user.read.datasource.initialSize}")
	private int initialSize;
	@Value("${user.read.datasource.minIdle}")
	private int minIdle;
	@Value("${user.read.datasource.maxActive}")
	private int maxActive;
	@Value("${user.read.datasource.maxWait}")
	private long maxWait;
	// 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
	@Value("${user.read.datasource.timeBetweenEvictionRunsMillis}")
	private long timeBetweenEvictionRunsMillis;
	// 配置一个连接在池中最小生存的时间，单位是毫秒
	@Value("${user.read.datasource.minEvictableIdleTimeMillis}")
	private long minEvictableIdleTimeMillis;
	@Value("${user.read.datasource.validationQuery}")
	private String validationQuery;
	@Value("${user.read.datasource.testWhileIdle}")
	private boolean testWhileIdle;
	@Value("${user.read.datasource.testOnBorrow}")
	private boolean testOnBorrow;
	@Value("${user.read.datasource.testOnReturn}")
	private boolean testOnReturn;
	@Value("${user.read.datasource.removeAbandoned}")
	private boolean removeAbandoned;
	@Value("${user.read.datasource.removeAbandonedTimeout}")
	private int removeAbandonedTimeout;
	@Value("${user.read.datasource.logAbandoned}")
	private boolean logAbandoned;
	/**
	 * 如果有可能变更，也可以做成配置项存放于配置中心
	 */
	private String aliasPackage = "com.huinong.truffle.payment.order.mono.entity";
	private String mapperLocation = "classpath:mapper/read/*.xml";
	private String mybatisConfigLocation = "mapper/mybatis-config.xml";

	@Override
	public String getAliasPackage()
	{
		return aliasPackage;
	}

	@Override
	public String getMapperLocation()
	{
		return mapperLocation;
	}

	@Override
	public String getMybatisConfigLocation()
	{
		return mybatisConfigLocation;
	}

	public boolean isRemoveAbandoned()
	{
		return removeAbandoned;
	}

	public void setRemoveAbandoned(boolean removeAbandoned)
	{
		this.removeAbandoned = removeAbandoned;
	}

	public int getRemoveAbandonedTimeout()
	{
		return removeAbandonedTimeout;
	}

	public void setRemoveAbandonedTimeout(int removeAbandonedTimeout)
	{
		this.removeAbandonedTimeout = removeAbandonedTimeout;
	}

	public boolean isLogAbandoned()
	{
		return logAbandoned;
	}

	public void setLogAbandoned(boolean logAbandoned)
	{
		this.logAbandoned = logAbandoned;
	}

	public int getInitialSize()
	{
		return initialSize;
	}

	public void setInitialSize(int initialSize)
	{
		this.initialSize = initialSize;
	}

	public int getMinIdle()
	{
		return minIdle;
	}

	public void setMinIdle(int minIdle)
	{
		this.minIdle = minIdle;
	}

	public int getMaxActive()
	{
		return maxActive;
	}

	public void setMaxActive(int maxActive)
	{
		this.maxActive = maxActive;
	}

	public long getMaxWait()
	{
		return maxWait;
	}

	public void setMaxWait(long maxWait)
	{
		this.maxWait = maxWait;
	}

	public long getTimeBetweenEvictionRunsMillis()
	{
		return timeBetweenEvictionRunsMillis;
	}

	public void setTimeBetweenEvictionRunsMillis(long timeBetweenEvictionRunsMillis)
	{
		this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
	}

	public long getMinEvictableIdleTimeMillis()
	{
		return minEvictableIdleTimeMillis;
	}

	public void setMinEvictableIdleTimeMillis(long minEvictableIdleTimeMillis)
	{
		this.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
	}

	public String getValidationQuery()
	{
		return validationQuery;
	}

	public void setValidationQuery(String validationQuery)
	{
		this.validationQuery = validationQuery;
	}

	public boolean isTestWhileIdle()
	{
		return testWhileIdle;
	}

	public void setTestWhileIdle(boolean testWhileIdle)
	{
		this.testWhileIdle = testWhileIdle;
	}

	public boolean isTestOnBorrow()
	{
		return testOnBorrow;
	}

	public void setTestOnBorrow(boolean testOnBorrow)
	{
		this.testOnBorrow = testOnBorrow;
	}

	public boolean isTestOnReturn()
	{
		return testOnReturn;
	}

	public void setTestOnReturn(boolean testOnReturn)
	{
		this.testOnReturn = testOnReturn;
	}

	public String getUrl()
	{
		return url;
	}

	public void setUrl(String url)
	{
		this.url = url;
	}

	public String getUser()
	{
		return user;
	}

	public void setUser(String user)
	{
		this.user = user;
	}

	public String getPassword()
	{
		return password;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	public String getDriverClass()
	{
		return driverClass;
	}

	public void setDriverClass(String driverClass)
	{
		this.driverClass = driverClass;
	}
}
