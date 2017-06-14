package com.huinong.truffle.payment.order.mono.component.redis.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import com.huinong.truffle.component.base.component.redis.config.RedisConfigIntf;

@Configuration
public class RedisConfig implements RedisConfigIntf {
	@Value("${redis.client.expireSeconds}")
	private int expireSeconds; // 默认的过期时间
	@Value("${redis.client.clusterNodes}")
	private String clusterNodes;// redis节点信息,多节点逗号 , 分割
	@Value("${redis.client.commandTimeout}")
	private int commandTimeout;// 超时时间
	
	public int getExpireSeconds() {
		return expireSeconds;
	}

	public void setExpireSeconds(int expireSeconds) {
		this.expireSeconds = expireSeconds;
	}

	public String getClusterNodes() {
		return clusterNodes;
	}

	public void setClusterNodes(String clusterNodes) {
		this.clusterNodes = clusterNodes;
	}

	public int getCommandTimeout() {
		return commandTimeout;
	}

	public void setCommandTimeout(int commandTimeout) {
		this.commandTimeout = commandTimeout;
	}

}
