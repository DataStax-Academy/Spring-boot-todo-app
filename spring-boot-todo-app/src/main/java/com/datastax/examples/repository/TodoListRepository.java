package com.datastax.examples.repository;

import com.datastax.examples.model.Task;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Target of the workshop is to implement CRUD repository with multiple strategies
 * we defined this interface to have specifications. 
 * 
 * @author Cedrick LUNVEN (@clunven)
 */
public interface TodoListRepository {
    
    /**
     * Find a task from its unique identifier.
     */
    Optional<Task> findById(UUID uid);

    /**
     * Create a new {@link Task} providing only a title.
     */
    void upsert(Task title);
    
    /**
     * Delete a task identifier
     */
    void delete(UUID uid);
    
    /**
     * List all available tasks.
     */
    List < Task > findAll();
    
    /**
     * Clean all records.
     */
    void deleteAll();
    
    

}
