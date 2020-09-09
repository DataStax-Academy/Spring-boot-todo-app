package com.datastax.examples.todo;

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
    
    /** Constants for table todo_tasks to be used in statements */
    String TABLE_TODO_TASKS     = "todo_tasks";
    String TASK_COL_UID         = "uid";
    String TASK_COL_TITLE       = "title";
    String TASK_COL_COMPLETED   = "completed";
    String TASK_COL_OFFSET      = "offset";
    
    /**
     * Find a task from its unique identifier.
     */
    Optional<Todo> findById(UUID uid);

    /**
     * Create a new {@link Todo} providing only a title.
     */
    void upsert(Todo title);
    
    /**
     * Delete a task identifier
     */
    void delete(UUID uid);
    
    /**
     * List all available tasks.
     */
    List < Todo > findAll();
    
    /**
     * Clean all records.
     */
    void deleteAll();
    
    

}
