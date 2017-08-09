package com.huinong.truffle;

import java.util.HashMap;
import java.util.Map;

import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.AprLifecycleListener;
import org.apache.coyote.http11.AbstractHttp11Protocol;
import org.apache.coyote.http11.Http11AprProtocol;
import org.apache.coyote.http11.Http11NioProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableAutoConfiguration
@EnableDiscoveryClient
@EnableFeignClients
@EnableSwagger2
public class OrderApplication {
	
	private static final Logger logger = LoggerFactory.getLogger(OrderApplication.class);
	
	
    public static void main(String[] args) {
    	SpringApplication application = new SpringApplication(OrderApplication.class);
    	Map<String, Object> defaultProperties = new HashMap<>();
    	// 读数据源dao层接口定义包
    	defaultProperties.put("read.dao.package", "com.huinong.truffle.payment.order.mono.dao.read");
    	// 写数据源dao层接口定义包
    	defaultProperties.put("write.dao.package", "com.huinong.truffle.payment.order.mono.dao.write");
    	application.setDefaultProperties(defaultProperties);
    	application.setWebEnvironment(true);
    	application.run(args);
        //new SpringApplicationBuilder(OrderApplication.class).web(true).run(args);
	}
    
    AprLifecycleListener arpLifecycle = null;
	 
	@Value("${server.protocol:org.apache.coyote.http11.Http11NioProtocol}")
	private String protocol;
	
	@Bean
	public EmbeddedServletContainerFactory embeddedServletContainerFactory()
	{
		TomcatEmbeddedServletContainerFactory tomcatFactory = new TomcatEmbeddedServletContainerFactory();
		arpLifecycle = new AprLifecycleListener();
		//tomcatFactory.setPort(8099);
		logger.info("==========protocol 协议:{}",protocol);
		tomcatFactory.setProtocol(protocol);//"org.apache.coyote.http11.Http11NioProtocol"
		tomcatFactory.addConnectorCustomizers(new TomcatConnectorCustomizer() {
			public void customize(Connector connector)
			{
				@SuppressWarnings("rawtypes")
				AbstractHttp11Protocol protocol = null;
				try {
					protocol = (Http11AprProtocol) connector.getProtocolHandler();
				} catch (Exception e) {
					protocol = (Http11NioProtocol) connector.getProtocolHandler();
				}
				protocol.setMaxTrailerSize(40 * 1024);
				// 设置最大连接数
				protocol.setMaxConnections(2000);
				// 设置最大线程数
				protocol.setMaxThreads(2000);
				protocol.setConnectionTimeout(30000);
			}
		});
		return tomcatFactory;
	}
	
	
	/** 加入swagger doc **/
	@Bean
	public Docket createRestApi(){
		return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo())
				.select().apis(RequestHandlerSelectors.basePackage("com.huinong.truffle.payment.order.mono.web"))
				.paths(PathSelectors.any()).build();
	}

	private ApiInfo apiInfo(){
		springfox.documentation.service.Contact c = new springfox.documentation.service.Contact("惠农科技", "http://www.cnhnb.com/", "luoguangxiang@cnhnkj.com");
		return new ApiInfoBuilder().title("HUI-NONG MICRO EXAMPLE TOPIC RESTful APIs").description("惠农微服务接入API用例列表")
				.termsOfServiceUrl("http://www.cnhnb.com").contact(c)
				.version("v1").build();
	}
}
