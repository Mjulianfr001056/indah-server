package com.indah.sandboxingserver.config;

import org.apache.spark.sql.SparkSession;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;

@Configuration
public class SparkConfig {

    @Bean
    @Scope("singleton")
    @Lazy
    public SparkSession sparkSession() {
        return SparkSession
                .builder()
                .appName("IndahApplication")
                .master("local[*]")
                .getOrCreate();
    }
}

