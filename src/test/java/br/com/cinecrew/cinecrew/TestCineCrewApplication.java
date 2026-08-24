package br.com.cinecrew.cinecrew;

import org.springframework.boot.SpringApplication;

public class TestCineCrewApplication {

    public static void main(String[] args) {
        SpringApplication.from(CineCrewApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
