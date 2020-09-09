package com.datastax.examples.todo;

import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.bindMarker;
import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.deleteFrom;
import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.selectFrom;
import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.truncate;
import static com.datastax.oss.driver.api.querybuilder.relation.Relation.column;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder;

/*
 * 
 * @author Cedrick LUNVEN (@clunven)
 */
@Repository("todobackend.repo.cassandra-driver")
public class TodoListRepositoryCassandraDriverImpl implements TodoListRepository {
    
    @Autowired
    public CqlSession cqlSession;
    
    private PreparedStatement psFindTask;
    private PreparedStatement psFindAll;
    private PreparedStatement psDeleteTask;
    private PreparedStatement psDeleteAll;
    
    /**
     * Default constructor
     */
    public TodoListRepositoryCassandraDriverImpl() {}
    
    /**
     * Constructoe with Sessions
     */
    public TodoListRepositoryCassandraDriverImpl(CqlSession cqlSession) {
        this.cqlSession = cqlSession;
        initStatements();
    }
    
    
    /** {@inheritDoc} */
    @Override
    public void upsert(Todo dto) {
        Assert.notNull(dto, "Task should not be null");
        Assert.notNull(dto.getUuid(), "Task 'uid' should not be null");
        /* 
         * No Prepared Statements Here simply define the needed query.
         *
         * INSERT into todo_tasks(uid, title, offset, completed)
         * VALUES (uuid(), 'One', 1, true);
         */
        SimpleStatement stmtInsertTask = SimpleStatement.builder(""
                + "INSERT INTO todo_tasks(uid, title, offset, completed)" 
                + "VALUES (?, ?, ?, ?)")
                .addPositionalValue(dto.getUuid())
                .addPositionalValue(dto.getTitle())
                .addPositionalValue(dto.getOrder())
                .addPositionalValue(dto.isCompleted())
                .build();
        cqlSession.execute(stmtInsertTask);
    }
    
    /** {@inheritDoc} */
    @Override
    public List<Todo> findAll() {
        List<Todo> targetList = cqlSession.execute(psFindAll.bind())
                                          .all().stream()
                                          .map(this::mapTaskRecord)
                                          .collect(Collectors.toList());
        Collections.sort(targetList); // Sorting based on 'order'
        return targetList;
    }
    
    /** {@inheritDoc} */
    @Override
    public void deleteAll() {
        cqlSession.execute(psDeleteAll.bind());
    }

    /** {@inheritDoc} */
    @Override
    public Optional<Todo> findById(UUID uid) {
        Assert.notNull(uid, "Task uid should not be null nor empty");
        ResultSet rs = cqlSession.execute(psFindTask.bind(uid));
        Row taskrecord = rs.one(); // Request with primary key = unicity
        if (null == taskrecord) return Optional.empty();
        return Optional.of(mapTaskRecord(taskrecord));
    }
    
    /** {@inheritDoc} */
    @Override
    public void delete(UUID uid) {
        Assert.notNull(uid, "Task uid should not be null nor empty");
        cqlSession.execute(psDeleteTask.bind(uid));
    }
    
    /**
     * You want to prepare you statements once to execute them faster later
     */
    @PostConstruct
    public void initStatements() {
        // Create table if not exist
        cqlSession.execute(SchemaBuilder
                .createTable(TodoListRepository.TABLE_TODO_TASKS)
                .ifNotExists()
                .withPartitionKey(TodoListRepository.TASK_COL_UID, DataTypes.UUID)
                .withColumn(TodoListRepository.TASK_COL_TITLE, DataTypes.TEXT)
                .withColumn(TodoListRepository.TASK_COL_COMPLETED, DataTypes.BOOLEAN)
                .withColumn(TodoListRepository.TASK_COL_OFFSET, DataTypes.INT)
                .build());
        
        if (psFindTask == null) {
            // SELECT * FROM todo_tasks where uid= d1d19715-a397-4975-bcb3-5107712db387;
            psFindTask = cqlSession.prepare(
                    selectFrom(TABLE_TODO_TASKS).all()
                    .where(column(TASK_COL_UID).isEqualTo(bindMarker(TASK_COL_UID)))
                    .build());
        }
        if (psFindAll == null) {
            // To empty a table, truncate is most performant
            psFindAll = cqlSession.prepare(selectFrom(TABLE_TODO_TASKS).all().build());
        }
        if (psDeleteTask == null) {
            // DELETE FROM todo_tasks where uid= d1d19715-a397-4975-bcb3-5107712db387;
            psDeleteTask = cqlSession.prepare(
                     deleteFrom(TABLE_TODO_TASKS)
                    .where(column(TASK_COL_UID).isEqualTo(bindMarker(TASK_COL_UID)))
                    .build());
        }
        if (psDeleteAll == null) {
            // To empty a table, truncate is most performant
            psDeleteAll = cqlSession.prepare(truncate(TABLE_TODO_TASKS).build());
        }
    }
    
    /**
     * Mapping from Cassandra Record to Object
     */
    private Todo mapTaskRecord(Row taskRecord) {
        Todo task = new Todo();
        task.setUuid(taskRecord.getUuid(TASK_COL_UID));
        task.setTitle(taskRecord.getString(TASK_COL_TITLE));
        task.setCompleted(taskRecord.getBoolean(TASK_COL_COMPLETED));
        task.setOrder(taskRecord.getInt(TASK_COL_OFFSET));
        return task;
    }
         
}
