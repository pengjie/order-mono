package com.huinong.truffle;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import springfox.documentation.swagger2.annotations.EnableSwagger2;


@EnableSwagger2
@EnableFeignClients
@EnableDiscoveryClient
@EnableCircuitBreaker
@SpringBootApplication(exclude = {
    DataSourceAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class
})
@EnableTransactionManagement
public class OrderApplication {
	
	public static void main(String[] args)
	{
		SpringApplication application = new SpringApplication(OrderApplication.class);
		application.setWebEnvironment(true);
		application.run(args);
	}
 
}