package com.es.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan("com.es.demo")
public class ESearchDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(ESearchDemoApplication.class, args);
	}

}

