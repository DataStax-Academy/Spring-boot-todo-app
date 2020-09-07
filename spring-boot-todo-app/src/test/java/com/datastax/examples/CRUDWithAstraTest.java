package com.datastax.examples;

import java.io.File;
import java.util.UUID;

import javax.annotation.PreDestroy;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.config.DriverConfigLoader;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.core.cql.SimpleStatementBuilder;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.examples.model.Task;
import com.datastax.examples.model.TodoAppSchema;
import com.datastax.examples.repository.TodoListRepository;
import com.datastax.examples.repository.TodoListRepositoryCassandraDriverImpl;

/**
 * Junit5 + Spring.
 * @author Cedrick LUNVEN (@clunven)
 */
@RunWith(JUnitPlatform.class)
@SpringJUnitConfig
public class CRUDWithAstraTest implements TodoAppSchema {

    /** Logger for the class. */
    private static Logger LOGGER = 
            LoggerFactory.getLogger(CRUDWithAstraTest.class);
    
    @TestConfiguration
    static class CrudWithCassandraDriverConfiguration {
  
        @Bean
        public CqlSession cqlSession() {
            String configFile = CRUDWithAstraTest.class.getResource("/application_test.conf").getFile();
            DriverConfigLoader configLoader = DriverConfigLoader.fromFile(new File(configFile));
            return CqlSession.builder().withConfigLoader(configLoader).build();
        }
        
        @Bean
        public TodoListRepository initRepo(CqlSession cqlSession) {
            TodoListRepositoryCassandraDriverImpl repo = new  TodoListRepositoryCassandraDriverImpl(cqlSession);
            repo.initStatements();
            return repo;
        }
    }
    
    @Autowired
    private CqlSession cqlSession;
    
    @Autowired
    private TodoListRepository todoRepo;
    
    /**
     * FIX THE TEST
     */
    @Test
    public void test_Insert() {
        /* 
         * ==========================================
         * Table Schema:
         * ==========================================
         * CREATE TABLE todoapp.todo_tasks (
         *  uid uuid,
         *  completed boolean,
         *  offset int,
         *  title text,
         *  PRIMARY KEY (uid)
         * );
         * 
         * ==========================================
         * Sample INSERT:
         * ==========================================
         * INSERT into todo_tasks(uid, title, offset, completed)
         * VALUES (uuid(), 'One', 1, true);
         */
        
        // Using CqlSession and SimpleStatement insert this is table todo_tasks
        UUID    sampleUID = UUID.randomUUID();
        System.out.println(sampleUID);
        String  sampleTitle = "A TASK";
        int     sampleOrder = 1;
        boolean sampleComplete = true;
        
        // Create here your statement and execute it
        //QueryBuilder.insertInto("todo_tasks").value(COL, value)
        SimpleStatement stmt = SimpleStatement.builder(""
                + "INSERT INTO todo_tasks (uid, title, offset, completed) "
                + "VALUES (?,?,?,?)")
                .addPositionalValue(sampleUID)
                .addPositionalValue(sampleTitle)
                .addPositionalValue(sampleOrder)
                .addPositionalValue(sampleComplete).
                build();
        cqlSession.execute(stmt);
        
        System.out.println("Task created with UUID: " + sampleUID);

        //SimpleStatement stmtInsertTask = SimpleStatement.builder(.sampleTitle...
        Assertions.assertFalse(todoRepo.findById(sampleUID).isEmpty());
    }
    
    // @Test
    // public void should_create_task_with_new_uid() {
    //     LOGGER.info("Starting CRUD Test");
    //     // Given an empty table
    //     UUID newUid = UUID.randomUUID();
    //     todoRepo.deleteAll();
    //     Assertions.assertEquals(0, todoRepo.findAll().size());
    //     LOGGER.info("+ Table has been emptied");
        
    //     // When adding a new Task
    //     todoRepo.upsert(new Task(newUid, "CrudWithCassandraDriverIntegrationTest", false, 1));
    //     // Then you should have a table of size 1
    //     Assertions.assertEquals(1, todoRepo.findAll().size());
    //     LOGGER.info("+ a New Task {} has been created", newUid);
        
    //     // Then you can find this task
    //     Assertions.assertFalse(todoRepo.findById(newUid).isEmpty());
    //     LOGGER.info("+ And we can retrieve it", newUid);
    //     // And WHEN you delete it
    //     todoRepo.delete(newUid);
    //     // Then this is empty again
    //     Assertions.assertEquals(0, todoRepo.findAll().size());
    //     LOGGER.info("+ And now it has been removed again", newUid);
    // }
    
    @PreDestroy
    public void closeSession() {
        if (null != cqlSession) {
            cqlSession.close();
        }
    }
    
}
