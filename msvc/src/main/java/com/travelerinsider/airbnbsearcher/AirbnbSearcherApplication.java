package com.travelerinsider.airbnbsearcher;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

import static org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO;

@SpringBootApplication
@EnableCaching
@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)
@Slf4j
public class AirbnbSearcherApplication {

	public static void main(String[] args) {
		log.info("Starting Airbnb Searcher service");
		ConfigurableApplicationContext context = SpringApplication.run(AirbnbSearcherApplication.class, args);
		log.info("Airbnb Searcher service started with {} active profile(s)", context.getEnvironment().getActiveProfiles().length);
	}

}
