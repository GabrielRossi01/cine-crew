package br.com.cinecrew.cinecrew;

import br.com.cinecrew.cinecrew.config.RsaKeyProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
public class CineCrewApplication {

    public static void main(String[] args) {
        SpringApplication.run(CineCrewApplication.class, args);
    }

}
