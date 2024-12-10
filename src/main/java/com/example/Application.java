package com.example;

import com.example.configurations.FilesConfigProperties;
import com.example.configurations.PGPConfigProperties;
import com.example.manager.PGPManager;
import com.example.manager.PGPManagerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.FileOutputStream;

@SpringBootApplication
public class Application {

	private static Logger logger = LoggerFactory.getLogger(Application.class);

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
		
		logger.info("Application started!");
	}

	@Component
	public class startApp implements CommandLineRunner {

		@Override
		public void run(String... strings) throws Exception {
			logger.info("running manager application");
			PGPManager pgpManager= new PGPManagerImpl(filesConfigProperties(), 	pgpConfigProperties());
			pgpManager.encryptFile();
			pgpManager.decryptFile();

		}
	}

	@Bean
	public FilesConfigProperties filesConfigProperties() {
		return new FilesConfigProperties();
	}
	@Bean
	public PGPConfigProperties pgpConfigProperties() {
		return new PGPConfigProperties();
	}

}
