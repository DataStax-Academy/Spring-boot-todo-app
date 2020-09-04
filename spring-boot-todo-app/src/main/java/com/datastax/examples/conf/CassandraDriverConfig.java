package com.datastax.examples.conf;

import com.datastax.oss.driver.api.core.CqlSession;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration of connectivity with Cassandra.
 */
@Configuration
public class CassandraDriverConfig {
    
    @Bean
    public CqlSession cqlSession() {
        return CqlSession.builder().build();
    }

}
