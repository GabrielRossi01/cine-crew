package br.com.cinecrew.cinecrew;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class CineCrewApplication {

    public static void main(String[] args) {
        SpringApplication.run(CineCrewApplication.class, args);
    }

}
