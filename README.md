## 🎓🔥 Build a Spring Java Microservice with Apache Cassandra 🔥🎓

[![Gitpod ready-to-code](https://img.shields.io/badge/Gitpod-ready--to--code-blue?logo=gitpod)](https://gitpod.io/#https://github.com/DataStax-Academy/Spring-boot-todo-app) 
[![License Apache2](https://img.shields.io/hexpm/l/plug.svg)](http://www.apache.org/licenses/LICENSE-2.0)
[![Discord](https://img.shields.io/discord/685554030159593522)](https://discord.com/widget?id=685554030159593522&theme=dark)

<img width="600" alt="Screenshot 2020-09-07 at 13 08 27" src="https://user-images.githubusercontent.com/20337262/92386378-9847d480-f10b-11ea-9782-d825f33a5182.png">


## Table of Contents

| Title  | Description
|---|---|
| **Slide deck** | [Slide deck for the workshop](slides/Presentation.pdf) |
| **0. Create your Astra instance** | [Create your Astra instance](#exercise-0---preparations) |
| **1. Test the connection to Astra** | [Test the connection to Astra](#exercise-1---test-the-connection-to-astra) |
| **2. Create schema and insert test data** | [Create schema and insert test data](#exercice-2---create-schema-and-test-some-inserts) |
| **3. Build the main app** | [Build the main app](#exercise-3---building-the-app) |
| **4. Test with Swagger** | [Test with Swagger](#exercise-4---test-the-api-with-swagger) |


## Exercise 0 - Preparations ##

**✅  Open Gitpod** : [Gitpod](http://www.gitpod.io/?utm_source=datastax&utm_medium=referral&utm_campaign=datastaxworkshops) is an IDE 100% online based on Eclipse Theia. To initialize your environment simply click on the button below *(CTRL + Click to open in new tab)*

[![Open in Gitpod](https://gitpod.io/button/open-in-gitpod.svg)](https://gitpod.io/#https://gitpod.io/#https://github.com/DataStax-Academy/Spring-boot-todo-app/)

Target url looks like  `https://<your_uid>.<your_region>.gitpod.io/#/#/workspace/Spring-boot-todo-app`. These URL's are dynamic and we cannot provide clickable links in advance. 

**✅  Create the Astra database** Create a database with keyspace `todoapp` on DataStax Astra.

If this is your first time using Astra, sign up here:

- [Registration Page](http://dtsx.io/workshop)

You can use your `Github`, `Google` accounts or register with an `email`

If you use these signup details, you won't need to make many changes when following along.

```
keyspace: todoapp
User: KVUser
Password: KVPassword
```

If you already have an Astra database, you can reuse it, but please create a new keyspace by name `todoapp`:

<img width="600" alt="Screenshot 2020-09-07 at 09 10 32" src="https://user-images.githubusercontent.com/20337262/92366057-902c6c80-f0ec-11ea-9903-897abc4a7ab8.png">

**✅  Download the secure connection bundle to Gitpod** 

Once your database is created and ready to use, copy the secure connect bundle link in the Connection Details link. Click on the copy icon next to `Download secure connect bundle`. This will save the link your clipboard. 

The link is only valid for a short time, so you will need to proceed to the next step quickly before it expires. Refreshing your browser will generate you a new valid link. 

<img width="600" alt="Screenshot 2020-09-07 at 09 10 01" src="https://user-images.githubusercontent.com/20337262/92366367-f6b18a80-f0ec-11ea-8532-da8644b49cd7.png">

We will use the copied link to download the bundle to gitpod via curl. Insert your copied link here:

```
curl -L "<insert link here>" > creds.zip
```

Expected output:

```bash
gitpod /workspace/Spring-boot-todo-app $ curl -L "https://datastax-cluster-config-prod.s3.us-east-2.amazonaws.com/..." > creds.zip  
  % Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
                                 Dload  Upload   Total   Spent    Left  Speed
100 12375  100 12375    0     0  21010      0 --:--:-- --:--:-- --:--:-- 21010
```

Verify that you have successfully downloaded the credentials and move them into the project root:

```
ls
mv creds.zip spring-boot-todo-app
cd spring-boot-todo-app/
```

Expected output:

```
gitpod /workspace/Spring-boot-todo-app $ ls
creds.zip  LICENSE  README.md  slides  spring-boot-todo-app
gitpod /workspace/Spring-boot-todo-app $ mv creds.zip spring-boot-todo-app
gitpod /workspace/Spring-boot-todo-app $ cd spring-boot-todo-app/
gitpod /workspace/Spring-boot-todo-app/spring-boot-todo-app $ ls
creds.zip  pom.xml  src
```

**✅  Install required dependencies**

Next, install all dependencies, but exclude tests, as we have not implemented them yet:

```
mvn clean install -Dmaven.test.skip=true
```

Your output should end like this:

```bash
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  8.125 s
[INFO] Finished at: 2020-09-02T09:11:36Z
[INFO] ------------------------------------------------------------------------
```

Great, we are all set up now. Proceed to Exercise 1.

[🏠 Back to Table of Contents](#table-of-contents)

## Exercise 1 - Test the connection to Astra ##

In the Gitpod explorer, locate the file ConnectivityToAstraExplicitTest.java

You will find it in the following directory:

```
/workspace/Spring-boot-todo-app/spring-boot-todo-app/src/test/java/com/datastax/examples
```

Modify the following section with your own credentials.

```java
    /** Settings. */
    public static String ASTRA_ZIP_FILE = "/workspace/Spring-boot-todo-app/spring-boot-todo-app/creds.zip";
    public static String ASTRA_USERNAME = "KVUser";
    public static String ASTRA_PASSWORD = "KVPassword";
    public static String ASTRA_KEYSPACE = "todoapp";
```

See here how we connect to Astra with the cqlSession:

```java
        // When connecting to ASTRA
        try (CqlSession cqlSession = CqlSession.builder()
                .withCloudSecureConnectBundle(Paths.get(ASTRA_ZIP_FILE))
                .withAuthCredentials(ASTRA_USERNAME, ASTRA_PASSWORD)
                .withKeyspace(ASTRA_KEYSPACE)
                .build()) {
            
            // Then connection is successfull
            LOGGER.info(" + [OK] - Connection Established to Astra with Keyspace {}", 
                    cqlSession.getKeyspace().get());
        }
```

Don't forget to save the file: There is no autosave with Gitpod. 

Let's run this test:

If you are not already there, change back into the project root:

```
cd /workspace/Spring-boot-todo-app/spring-boot-todo-app/
```

Then run the test:

```
mvn test -Dtest=com.datastax.examples.ConnectivityToAstraExplicitTest#should_connect_to_Astra_static
```

Yay, we are connected:

```
-------------------------------------------------------
 T E S T S
-------------------------------------------------------
Picked up JAVA_TOOL_OPTIONS: -Xmx2254m
Running com.datastax.examples.ConnectivityToAstraExplicitTest
08:12:21.473 INFO  com.datastax.examples.ConnectivityToAstraExplicitTest :  + [OK] - Connection Established to Astra with Keyspace todoapp
Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 10.622 sec - in com.datastax.examples.ConnectivityToAstraExplicitTest

Results :

Tests run: 1, Failures: 0, Errors: 0, Skipped: 0

[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  18.313 s
[INFO] Finished at: 2020-09-07T08:12:24Z
[INFO] ------------------------------------------------------------------------

```

Now let's configure the driver with the configuration file for the next test:

Locate the file: `ConnectivityToAstraWithConfTest.java` (in the same test source folder)

Check this section:

```java
        // Config loader from file
        DriverConfigLoader loader = DriverConfigLoader.fromFile(
                new File(ConnectivityToAstraWithConfTest.class.getResource("/application.conf").getFile()));
        
        // Use it to create the session
        try (CqlSession cqlSession = CqlSession.builder().withConfigLoader(loader).build()) {
            LOGGER.info(" + [OK] - Connection Established to Astra with Keyspace {}", 
                    cqlSession.getKeyspace().get());
        }
```

Here we are loading the config from a file named `application.conf`
And then we are passing this loaded configuration to the cqlSession.

Locate the `application.conf` file in the following resources folder

```
/workspace/Spring-boot-todo-app/spring-boot-todo-app/src/main/resources
```

We now define our cluster connection settings in the file `application.conf`:

```
    # Here please enter your keyspace
    session-keyspace = todoapp
    cloud {
      # Path as defined in gitpod 
      secure-connect-bundle = /workspace/Spring-boot-todo-app/spring-boot-todo-app/creds.zip
    }
  }

  advanced {
    auth-provider {
      class = PlainTextAuthProvider
      # Here please user and password
      username = KVUser
      password = KVPassword
    }
```

While we are at it, we might as well make a couple more changes that allow for slow connections:

See this entry in the `advanced` section:

```
    connection {
      init-query-timeout = 10 seconds
      set-keyspace-timeout = 10 seconds
    }
    control-connection.timeout = 10 seconds
```

Don't forget to save the `application.conf`, and then we can test the modified connection. Change back into the project root if necessary:

```
cd /workspace/Spring-boot-todo-app/spring-boot-todo-app/
```

Then run the test:

```
mvn test -Dtest=com.datastax.examples.ConnectivityToAstraWithConfTest#should_connect_to_Astra_withConfig
```

Success:

```
-------------------------------------------------------
 T E S T S
-------------------------------------------------------
Picked up JAVA_TOOL_OPTIONS: -Xmx2254m
Running com.datastax.examples.ConnectivityToAstraWithConfTest
08:18:21.764 INFO  com.datastax.examples.ConnectivityToAstraWithConfTest :  + [OK] - Connection Established to Astra with Keyspace todoapp
Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 6.35 sec - in com.datastax.examples.ConnectivityToAstraWithConfTest

Results :

Tests run: 1, Failures: 0, Errors: 0, Skipped: 0

[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  9.383 s
[INFO] Finished at: 2020-09-07T08:18:24Z
[INFO] ------------------------------------------------------------------------
```

[🏠 Back to Table of Contents](#table-of-contents)


## Exercice 2 - Create schema and test some inserts

Now that we have configured the driver config file, we can use the session to execute statements. First we will execute schema statements, in order to create the correct schema.

Locate the file `CreateSchemaInAstraTest.java` in the test source folder:

```
/workspace/Spring-boot-todo-app/spring-boot-todo-app/src/test/java/com/datastax/examples
```

Inspect the code:


We are using the driver's schema builder to build this simple statement to create the required table:

```java
            SimpleStatement stmtCreateTable = SchemaBuilder.createTable(TodoListRepository.TABLE_TODO_TASKS).ifNotExists()
                    .withPartitionKey(TodoListRepository.TASK_COL_UID, DataTypes.UUID)
                    .withColumn(TodoListRepository.TASK_COL_TITLE, DataTypes.TEXT)
                    .withColumn(TodoListRepository.TASK_COL_COMPLETED, DataTypes.BOOLEAN)
                    .withColumn(TodoListRepository.TASK_COL_OFFSET, DataTypes.INT)
                    .build();
            
            // When creating the table
            cqlSession.execute(stmtCreateTable);
```

The table is defined in this public repository (located in `/workspace/Spring-boot-todo-app/spring-boot-todo-app/src/main/java/com/datastax/examples/todo/TodoListRepository.java`)

```
public interface TodoListRepository {
    
    /** Constants for table todo_tasks to be used in statements */
    String TABLE_TODO_TASKS     = "todo_tasks";
    String TASK_COL_UID         = "uid";
    String TASK_COL_TITLE       = "title";
    String TASK_COL_COMPLETED   = "completed";
    String TASK_COL_OFFSET      = "offset";
```

Let's run this test. This will create the schema in our database.

```
mvn test -Dtest=com.datastax.examples.CreateSchemaInAstraTest#should_create_expected_table
```

Here is the expected output from this test:

```
-------------------------------------------------------
 T E S T S
-------------------------------------------------------
Picked up JAVA_TOOL_OPTIONS: -Xmx2254m
Running com.datastax.examples.CreateSchemaInAstraTest
08:23:00.026 INFO  com.datastax.examples.CreateSchemaInAstraTest : Connection Established to Astra with Keyspace 'todoapp'
08:23:00.068 INFO  com.datastax.examples.CreateSchemaInAstraTest : Table 'todo_tasks' has been created (if needed).
Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 6.173 sec - in com.datastax.examples.CreateSchemaInAstraTest

Results :

Tests run: 1, Failures: 0, Errors: 0, Skipped: 0

[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  9.126 s
[INFO] Finished at: 2020-09-07T08:23:02Z
[INFO] ------------------------------------------------------------------------
```

We can verify that the table has been created by logging into the Astra CQLSH console:

<img width="500" alt="Screenshot 2020-09-07 at 09 24 44" src="https://user-images.githubusercontent.com/20337262/92366244-cbc73680-f0ec-11ea-8d49-e83daf6ab444.png">

```
Logging in with cqlsh
User: KVUser
Password:
Connected to caas-cluster at caas-cluster-caas-dc-service:9042.
[cqlsh 6.8.0 | DSE 6.8.4.145 | CQL spec 3.4.5 | DSE protocol v2]
Use HELP for help.
KVUser@cqlsh> use todoapp;
KVUser@cqlsh:todoapp> describe table todoapp.todo_tasks ;

CREATE TABLE todoapp.todo_tasks (
    uid uuid PRIMARY KEY,
    completed boolean,
    offset int,
    title text
) WITH additional_write_policy = 'NONE'
    AND bloom_filter_fp_chance = 0.01
    AND caching = {'keys': 'ALL', 'rows_per_partition': 'NONE'}
    AND comment = ''
    AND compaction = {'class': 'org.apache.cassandra.db.compaction.SizeTieredCompactionStrategy', 'max_threshold': '32', 'min_threshold': '4'}
    AND compression = {'enabled': 'false'}
    AND crc_check_chance = 1.0
    AND default_time_to_live = 0
    AND gc_grace_seconds = 864000
    AND max_index_interval = 2048
    AND memtable_flush_period_in_ms = 0
    AND min_index_interval = 128
    AND nodesync = {'enabled': 'true', 'incremental': 'true'}
    AND read_repair = 'BLOCKING'
    AND speculative_retry = 'NONE';

KVUser@cqlsh:todoapp>
```



Let's insert some test data:

Locate the test CRUDWithAstraTest.java in the test source folder.

Inspect the test code where we insert the data:

```java
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
```

Make some changes to the task title, for example. 

To test, run this command.

```
mvn test -Dtest=com.datastax.examples.CRUDWithAstraTest#test_Insert
```

Check in the Astra cql shell to check if the data is there.

Success!

Now we can move on to implementing our rest controllers with Spring boot.

[🏠 Back to Table of Contents](#table-of-contents)


## Exercise 3 - Building the App ##

Let's move away from the tests and have a look at our Spring application.

Let's start with the driver configuration. The bean is defined and configured in the CassandraDriverConfig.java class:

```java
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
```

By default, the driver will take the application.conf from the resources folder to configure this connection.


As before, we are using the following model for the `todo_tasks`:

```java
public interface TodoListRepository {
  
    /** Constants for table todo_tasks */
    String TABLE_TODO_TASKS     = "todo_tasks";
    String TASK_COL_UID         = "uid";
    String TASK_COL_TITLE       = "title";
    String TASK_COL_COMPLETED   = "completed";
    String TASK_COL_OFFSET      = "offset";
  
}
```

The public interface `TodoListRepository.java` also defines the API that we will expose:

```
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
```

The class `TodoListRepositoryCassandraDriverImpl.java` is the Cassandra specific implementation of the interface.

This is a big file, so won't copy it here. In a nutshell, it implements each of the interface functions so they can be executed via the driver.

```java
@Repository("todobackend.repo.cassandra-driver")
public class TodoListRepositoryCassandraDriverImpl implements TodoListRepository, TodoAppSchema {
    
    @Autowired
    public CqlSession cqlSession;
    ...
```

For example, here is the insert and update of a Task:

```java
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
```

And finally we have the RestController `TodoListRestController.java`, which implements the REST API.

```java
@RestController
@RequestMapping("/api/v1/todos")
public class TodoListRestController {
    
    @Autowired
    @Qualifier("todobackend.repo.cassandra-driver")
    ...
```

Here for example the creation of a new task:

```java
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
```

Now run the application:

```
mvn spring-boot:run
```

This is the expected output:

```
[INFO] Attaching agents: []
Picked up JAVA_TOOL_OPTIONS: -Xmx2254m

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::        (v2.3.0.RELEASE)

11:47:59.792 INFO  com.datastax.examples.Application             : Starting Application on ws-a9aeb9ce-763d-4a96-949c-ab7b29781a45 with PID 2238 (/workspace/Spring-boot-todo-app/spring-boot-todo-app/target/classes started by gitpod in /workspace/Spring-boot-todo-app/spring-boot-todo-app)
11:47:59.797 INFO  com.datastax.examples.Application             : No active profile set, falling back to default profiles: default
11:48:04.280 INFO  com.datastax.examples.Application             : Started Application in 4.955 seconds (JVM running for 5.616)
```

The API is now available on port 8080.

Gitpod encourages you to open a browser:

<img width="600" alt="Screenshot 2020-09-09 at 12 55 50" src="https://user-images.githubusercontent.com/20337262/92595431-41253980-f29c-11ea-93d3-8c067ea58bd8.png">

Note the custom URL that gitpod generates for public access through port 8080, the port is appended at the beginning of the link:

```
https://8080-<your-uuid>.<your-region>.gitpod.io/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config
```

You should see the Swagger UI:

<img width="600" alt="Screenshot 2020-09-09 at 12 54 13" src="https://user-images.githubusercontent.com/20337262/92595512-66b24300-f29c-11ea-85b7-bab703c57d6a.png">

Well done! 

[🏠 Back to Table of Contents](#table-of-contents)


## Exercise 4 - Test the API with Swagger ##

Play with Swagger.

To list all todos that are currently in the database:

Select the option `GET /api/v1/todos/ Retrieve the complete list of tasks`

<img width="600" alt="Screenshot 2020-09-07 at 16 24 43" src="https://user-images.githubusercontent.com/20337262/92402503-92f88300-f127-11ea-8f21-382580d776e2.png">

Then opt for `Try it out` and `Execute`.

<img width="600" alt="Screenshot 2020-09-07 at 16 24 54" src="https://user-images.githubusercontent.com/20337262/92402588-b4596f00-f127-11ea-8a7c-0980a1e0507d.png">

You will see the used `curl` command (you can also try this in any terminal), and the response from the server.

<img width="600" alt="Screenshot 2020-09-07 at 16 25 19" src="https://user-images.githubusercontent.com/20337262/92402638-caffc600-f127-11ea-9f06-6e44bd5477c1.png">

Try inserting some tasks and compare with output in the Astra cqlsh console. 

Thanks for doing the exercises!

[🏠 Back to Table of Contents](#table-of-contents)


## Bonus Exercise - Test with Todo MVC, client GUI and API specs ##

To test with the Todo MVC client and spec tests, we need to make changes to the data model in order to support the test specs. The model has been simplified for this particular workshop.

Check the instructions in this repository for the details re Todo MVC.

https://github.com/DataStax-Academy/microservices-java-workshop-online







