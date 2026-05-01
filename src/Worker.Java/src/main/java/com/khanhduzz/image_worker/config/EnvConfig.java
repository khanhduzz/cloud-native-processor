package com.khanhduzz.image_worker.config;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EnvConfig {

    @PostConstruct
    public void loadEnv() {
        Dotenv dotenv = Dotenv.configure()
                .directory("./")
                .ignoreIfMissing()
                .load();

        // Feed the .env variables into Spring's Environment
        dotenv.entries().forEach(entry -> {
            if (System.getProperty(entry.getKey()) == null) {
                System.setProperty(entry.getKey(), entry.getValue());
                // System.out.println("---> Loaded env variable: " + entry.getKey() + "=" +
                // entry.getValue());
            }
        });

        System.out.println("---> Successfully loaded .env from root directory");
    }
}
