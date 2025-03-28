package com.example.shopping;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableCaching
@EnableScheduling
@EnableJpaAuditing
@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.example.shopping.domain.user.repository")
public class ShoppingApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(ShoppingApplication.class, args);
	}
	
}
