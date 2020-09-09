package com.datastax.examples;

import java.io.File;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.examples.todo.TodoListRepository;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.config.DriverConfigLoader;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder;

@RunWith(JUnitPlatform.class)
public class CreateSchemaInAstraTest {

    /** Logger for the class. */
    private static Logger LOGGER = LoggerFactory.getLogger(CreateSchemaInAstraTest.class);

    @Test
    public void should_create_expected_table() {
        
        // Config loader from file
        DriverConfigLoader loader = DriverConfigLoader.fromFile(
                new File(CreateSchemaInAstraTest.class.getResource("/application.conf").getFile()));
        
        // Use it to create the session
        try (CqlSession cqlSession = CqlSession.builder().withConfigLoader(loader).build()) {
        
            LOGGER.info("Connection Established to Astra with Keyspace '{}'", 
                    cqlSession.getKeyspace().get());
            
            // Given a statement
            
            SimpleStatement stmtCreateTable = SchemaBuilder.createTable(TodoListRepository.TABLE_TODO_TASKS).ifNotExists()
                    .withPartitionKey(TodoListRepository.TASK_COL_UID, DataTypes.UUID)
                    .withColumn(TodoListRepository.TASK_COL_TITLE, DataTypes.TEXT)
                    .withColumn(TodoListRepository.TASK_COL_COMPLETED, DataTypes.BOOLEAN)
                    .withColumn(TodoListRepository.TASK_COL_OFFSET, DataTypes.INT)
                    .build();
            
            // When creating the table
            cqlSession.execute(stmtCreateTable);
            
            // Then table is created
            LOGGER.info("Table '{}' has been created (if needed).", TodoListRepository.TABLE_TODO_TASKS);
        }
    }
}
