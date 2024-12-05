package com.myinsta.discovery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class EurekaDiscoveryApplication {
    private static final Logger logger = LoggerFactory.getLogger(EurekaDiscoveryApplication.class);

	public static void main(String[] args) {
        logger.debug("EurekaDiscoveryApplication starting");
		SpringApplication.run(EurekaDiscoveryApplication.class, args);
        logger.debug("EurekaDiscoveryApplication started succesfully.");

	}

}
