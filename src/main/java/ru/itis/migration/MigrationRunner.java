package ru.itis.migration;

import liquibase.integration.spring.SpringLiquibase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("migrate")
@Slf4j
public class MigrationRunner implements CommandLineRunner {

    private final SpringLiquibase liquibase;
    private final ConfigurableApplicationContext context;

    public MigrationRunner(SpringLiquibase liquibase, ConfigurableApplicationContext context) {
        this.liquibase = liquibase;
        this.context = context;
    }

    @Override
    public void run(String... args) {
        log.info("Миграции базы данных применены: {}", liquibase.getChangeLog());
        System.exit(SpringApplication.exit(context));
    }
}
