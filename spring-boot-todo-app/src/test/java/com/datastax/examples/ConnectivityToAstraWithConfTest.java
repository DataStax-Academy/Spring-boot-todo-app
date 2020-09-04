package com.datastax.examples;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.config.DriverConfigLoader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * First Connectivity test with Astra.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
@RunWith(JUnitPlatform.class)
public class ConnectivityToAstraWithConfTest {

    /** Logger for the class. */
    private static Logger LOGGER = LoggerFactory.getLogger(ConnectivityToAstraWithConfTest.class);
    
    @Test
    @DisplayName("Test connectivity to Astra delegate file")
    public void should_connect_to_Astra_withConfig() {

        // Config loader from file
        DriverConfigLoader loader = DriverConfigLoader.fromFile(
                new File(ConnectivityToAstraWithConfTest.class.getResource("/application_test.conf").getFile()));
        
        // Use it to create the session
        try (CqlSession cqlSession = CqlSession.builder().withConfigLoader(loader).build()) {
            LOGGER.info(" + [OK] - Connection Established to Astra with Keyspace {}", 
                    cqlSession.getKeyspace().get());
        }
    }

}
