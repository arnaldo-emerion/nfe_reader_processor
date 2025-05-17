package br.com.arcasoftware.sbs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableCaching
@EnableTransactionManagement
public class SpringBootSqsApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootSqsApplication.class, args);
	}

}
