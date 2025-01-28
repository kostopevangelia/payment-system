package com.evangeliakostop.paymentsystem.config.mongo;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "mongo.properties.config")
public class MongoPropertiesConfig {

    private String connectionUrl;
    private String authDb;
    private String username;
    private String password;
}
