package com.t1.profile;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
public class ProfileApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProfileApplication.class, args);
	}

}
