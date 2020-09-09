package com.datastax.examples.todo;

import com.datastax.oss.driver.api.core.DriverException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.*;

@CrossOrigin(
  methods = {PUT, POST, GET, OPTIONS, DELETE, PATCH},
  maxAge = 3600,
  allowedHeaders = {"x-requested-with", "origin", "content-type", "accept"},
  origins = "*"
)
@RestController
@RequestMapping("/api/v1/todos")
@Tag(name = "Todos", description = "Implement CRUD operations for Todo Tasks")
public class TodoListRestController {
    
    /** Logger for the class. */
    private static final Logger logger = LoggerFactory.getLogger(TodoListRestController.class);
    
    @Autowired
    @Qualifier("todobackend.repo.cassandra-driver")
    private TodoListRepository todoRepository;
    
    /**
     * Default constructor.
     */
    public TodoListRestController() {}

    /**
     * Constructor.
     */
    public TodoListRestController(TodoListRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    // --- Operation on the list --

    /**
     * Retrieve all tasks (GET)
     */
    @Operation(
            summary = "Retrieve the complete list of Tasks",
            description = "List all records in the tables",
            tags = { "todos" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                         description = "The list has been retrieved even if empty no error",
                         content = @Content(array = @ArraySchema(schema = @Schema(implementation = Todo.class)))) })
    @RequestMapping(
            value = "/",
            method = GET,
            produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Todo>> findAll(HttpServletRequest request) {
        logger.info("List all task in the db: {}", rewriteUrl(request) + "?" + request.getQueryString());
        return ResponseEntity.ok(todoRepository.findAll()
                .stream().collect(Collectors.toList()));
    }

    /**
     * Delete all tasks (DELETE)
     */
    @Operation(
            summary = "Delete all tasks in one go",
            description = "Clear the storage",
            tags = { "todos", "delete" })
    @ApiResponses(value = { @ApiResponse(responseCode = "204", description = "No results") } )
    @RequestMapping(value = "/", method = DELETE)
    public ResponseEntity<Void> deleteAll(HttpServletRequest request) {
        logger.info("Delete all task in the db: {}", rewriteUrl(request) + "?" + request.getQueryString());
        todoRepository.deleteAll();
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // --- Unitary operation --

    /**
     * CREATE = Create a new Task (POST)
     */
    @Operation(
            summary = "Create a new task",
            description = "POST is the proper http verb when you cannot provide the full URL (including id)",
            tags = { "create" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                            description = "The task has been successfully created",
                            content = @Content(schema = @Schema(implementation = Todo.class))),
            @ApiResponse(responseCode = "400", description = "Title is blank but is mandatory"),
            @ApiResponse(responseCode = "500", description = "An error occur in storage") })
    @RequestMapping(
            value = "/",
            method = POST,
            produces = APPLICATION_JSON_VALUE,
            consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<Todo> create(HttpServletRequest request,
            @RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Only field <b>title</b> is required in the following JSON object", 
                    required = true, 
                    content = @Content(schema = @Schema(implementation = Todo.class)))
            Todo taskCreationRequest)
    throws URISyntaxException {
        Assert.notNull(taskCreationRequest, "You must provide a Task in BODY");
        Assert.hasLength(taskCreationRequest.getTitle(), "Title is a required field to create a task");
        logger.info("Create new Task at {} with title {}",
                request.getRequestURL().toString(), taskCreationRequest.getTitle());
        Todo dto = new Todo(UUID.randomUUID(),
                taskCreationRequest.getTitle(),
                taskCreationRequest.isCompleted(),
                taskCreationRequest.getOrder());
        todoRepository.upsert(dto);
        // Created
        return ResponseEntity.ok(dto);
    }

    /**
     * READ = Find a Task by its id (GET)
     */
    @RequestMapping(
            value = "/{taskId}",
            method = GET,
            produces = APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Get details of a task if exists",
            description = "Retrieve a tasks based on its identifier",
            tags = { "Task" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(implementation = Todo.class))),
            @ApiResponse(responseCode = "400", description = "UUID is blank or contains invalid characters (expecting valid UUID)"),
            @ApiResponse(responseCode = "404", description = "Task not found") })
    public ResponseEntity<Todo> read(HttpServletRequest request,
            @Parameter(name="taskId",
                     description="Unique identifier for the task",
                     example = "6f6c5b47-4e23-4437-ada8-d0a6f79330a2",
                     required=true )
            @PathVariable(value = "taskId") String taskId) {
        logger.info("Find a task with its id {}", rewriteUrl(request) + "?" + request.getQueryString());
        Assert.hasLength(taskId, "TaskId id is required and should not be null");
        Optional<Todo> myTask = todoRepository.findById(UUID.fromString(taskId));
        // Routing Result
        if (!myTask.isPresent()) {
            logger.warn("Task with uid {} has not been found", taskId);
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(myTask.get());
    }

    /**
     * Update an existing Task (PATCH)
     */
    @Operation(
            summary = "Update an existing task",
            description = "PATCH when you have id and providing body",
            tags = { "update" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(implementation = Todo.class))),
            @ApiResponse(responseCode = "400", description = "Json body not valid"),
            @ApiResponse(responseCode = "404", description = "Task UUID not found") })
    @RequestMapping(
            value = "/{taskId}",
            method = PATCH,
            produces = APPLICATION_JSON_VALUE,
            consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<Todo> update(HttpServletRequest request,
            @Parameter(name="taskId", required=true,
            description="Unique identifier for the task",
            example = "6f6c5b47-4e23-4437-ada8-d0a6f79330a2")
            @PathVariable(value = "taskId") String taskId,
            @RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Update all fields if needed",
                    required = true,
                    content = @Content(schema = @Schema(implementation = Todo.class)))
            Todo task)
    throws URISyntaxException {
        Assert.notNull(task, "You must provide a Task in BODY");
        logger.info("Updating task {}", taskId);
        Optional<Todo> myTask = todoRepository.findById(UUID.fromString(taskId));
        // Routing Result
        if (!myTask.isPresent()) {
            logger.warn("Task with uid {} has not been found", taskId);
            return ResponseEntity.notFound().build();
        }
        Todo existing = myTask.get();
        String newTitle =  task.getTitle();
        if (null != newTitle && !"".equals(newTitle)) {
            existing.setTitle(newTitle);
        }
        existing.setCompleted(task.isCompleted());
        existing.setOrder(task.getOrder());
        todoRepository.upsert(existing);
        return ResponseEntity.ok(myTask.get());
    }

    /**
     * Delete a Task by its id
     */
    @RequestMapping(value = "/{taskId}", method = DELETE)
    @Operation(summary = "Delete a task from its id of exists", description = "Delete a task from its id of exists")
    @ApiResponses(value = { @ApiResponse(responseCode = "204", description = "No results") } )
    public ResponseEntity<Void> delete(HttpServletRequest request,
            @Parameter(name="taskId", required=true,
                       description="Unique identifier for the task",
                       example = "6f6c5b47-4e23-4437-ada8-d0a6f79330a2")
            @PathVariable(value = "taskId") String taskId) {
        logger.info("Delete a task with its id {}", request.getRequestURL().toString());
        Assert.hasLength(taskId, "TaskId id is required and should not be null");
        Optional<Todo> myTask = todoRepository.findById(UUID.fromString(taskId));
        // Routing Result
        if (!myTask.isPresent()) {
            logger.warn("Task with uid {} has not been found", taskId);
            return ResponseEntity.notFound().build();
        }
        todoRepository.delete(UUID.fromString(taskId));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Converts {@link IllegalArgumentException} into HTTP 400 bad parameter
     * the response body.
     *
     * @param e The {@link DriverException}.
     * @return The error message to be used as response body.
     */
    @ExceptionHandler(value = IllegalArgumentException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public String _errorBadRequestHandler(IllegalArgumentException ex) {
        return "Invalid Parameter: " + ex.getMessage();
    }

    /**
     * Converts {@link DriverException} into HTTP 500 error codes and outputs the error message as
     * the response body.
     *
     * @param e The {@link DriverException}.
     * @return The error message to be used as response body.
     */
    @ExceptionHandler(DriverException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String _errorDriverHandler(DriverException e) {
      return e.getMessage();
    }

    /**
     * As isSecure is still false and https is enforce by gitpod let's
     * change it.
     */
    private String rewriteUrl(HttpServletRequest request) {
        String myUrl = request.getRequestURL().toString();
        if (myUrl.contains("gitpod")) {
            myUrl = myUrl.replaceAll("http", "https");
        }
        return myUrl;
    }

}
