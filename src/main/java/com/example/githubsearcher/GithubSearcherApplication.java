package com.example.githubsearcher;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(info = @Info(title = "GitHub Repository Searcher API", version = "1.0.0"))
@SpringBootApplication
public class GithubSearcherApplication {

    public static void main(String[] args) {
        SpringApplication.run(GithubSearcherApplication.class, args);
    }
}
