package com.happysadman.reactivetest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.blockhound.BlockHound;

@SpringBootApplication
public class ReactiveTestApplication {

	static {
		BlockHound.install();
	}

	public static void main(String[] args) {
		SpringApplication.run(ReactiveTestApplication.class, args);
	}

}
