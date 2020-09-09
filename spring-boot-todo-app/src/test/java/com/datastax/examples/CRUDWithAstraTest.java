package com.datastax.examples;

import java.io.File;
import java.util.UUID;

import javax.annotation.PreDestroy;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import com.datastax.examples.todo.TodoListRepository;
import com.datastax.examples.todo.TodoListRepositoryCassandraDriverImpl;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.config.DriverConfigLoader;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;

/**
 * Junit5 + Spring.
 * @author Cedrick LUNVEN (@clunven)
 */
@RunWith(JUnitPlatform.class)
@SpringJUnitConfig
public class CRUDWithAstraTest {
    
    @TestConfiguration
    static class CrudWithCassandraDriverConfiguration {
  
        @Bean
        public CqlSession cqlSession() {
            String configFile = CRUDWithAstraTest.class.getResource("/application.conf").getFile();
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
    
    @PreDestroy
    public void closeSession() {
        if (null != cqlSession) {
            cqlSession.close();
        }
    }
    
}
