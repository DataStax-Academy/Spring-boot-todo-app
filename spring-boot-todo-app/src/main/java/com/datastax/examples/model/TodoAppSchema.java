package com.datastax.examples.model;

/**
 * Having all schema info in a single place is a recommended
 * path when you need to update a column.
 * 
 * Tips: Constants in interfaces can be inherited in multiple locations.
 * 
 * @author Cedrick LUNVEN (@clunven)
 */
public interface TodoAppSchema {
  
    /** Constants for table todo_tasks */
    String TABLE_TODO_TASKS     = "todo_tasks";
    String TASK_COL_UID         = "uid";
    String TASK_COL_TITLE       = "title";
    String TASK_COL_COMPLETED   = "completed";
    String TASK_COL_OFFSET      = "offset";
    
    

}
