//package ru.ekp.redis.config;
//
//import liquibase.integration.spring.SpringLiquibase;
//import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
//import org.springframework.boot.context.properties.EnableConfigurationProperties;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.DependsOn;
//
//import javax.sql.DataSource;
//
//@Configuration
//@EnableConfigurationProperties(LiquibaseProperties.class)
//public class LiquibaseConfig {
//
//    @Bean
//    @DependsOn("entityManagerFactory")  // Запускаем Liquibase ПОСЛЕ EntityManagerFactory
//    public SpringLiquibase liquibase(DataSource dataSource, LiquibaseProperties properties) {
//        SpringLiquibase liquibase = new SpringLiquibase();
//        liquibase.setDataSource(dataSource);
//        liquibase.setChangeLog(properties.getChangeLog());
//        liquibase.setContexts(properties.getContexts().toString());
//        liquibase.setDefaultSchema(properties.getDefaultSchema());
//        liquibase.setDropFirst(properties.isDropFirst());
//        liquibase.setShouldRun(properties.isEnabled());
//        liquibase.setChangeLogParameters(properties.getParameters());
//        liquibase.setRollbackFile(properties.getRollbackFile());
//        return liquibase;
//    }
//}