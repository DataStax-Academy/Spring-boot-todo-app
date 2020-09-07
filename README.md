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

Create a database with keyspace `todoapp` on DataStax Astra.

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

Once your database is created and ready to use, download the secure connect bundle from the main connection dialog page and save it locally. We will need to upload this bundle in the next step. Keep it as a zip, there is no need to unzip the file.

<img width="600" alt="Screenshot 2020-09-07 at 09 10 01" src="https://user-images.githubusercontent.com/20337262/92366367-f6b18a80-f0ec-11ea-8532-da8644b49cd7.png">

To get coding, start gitpod. You will need to be signed in to Github:

**✅  Open gitpod** : [Gitpod](http://www.gitpod.io/?utm_source=datastax&utm_medium=referral&utm_campaign=datastaxworkshops) is an IDE 100% online based on Eclipse Theia. To initialize your environment simply click on the button below *(CTRL + Click to open in new tab)*

[![Open in Gitpod](https://gitpod.io/button/open-in-gitpod.svg)](https://gitpod.io/#https://github.com/DataStax-Academy/Spring-boot-todo-app/tree/master/spring-boot-todo-app)

Target url looks like  `https://<your_uid>.<your_region>.gitpod.io/#/#/workspace/Spring-boot-todo-app`. These URL's are dynamic and we cannot provide clickable links in advance. You will need to copy-paste `<your_uid>.<your_region>` as we will insert them in each URL during the exercises.


First step: Upload your Astra secure bundle.

To upload a file to gitpod, you need to select a folder in the left hand explorer panel first. Select the folder `spring-boot-todo-app`.

Then select `File -> Upload Files...` from the header menu and upload the secure bundle that you saved earlier.

You should now see it in the `spring-boot-todo-app` folder

```bash
gitpod /workspace/Spring-boot-todo-app/spring-boot-todo-app $ ls
pom.xml  secure-connect-killrvideocluster.zip  src  target
```

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
    /** Settings. */
    public static String ASTRA_ZIP_FILE = "/workspace/Spring-boot-todo-app/spring-boot-todo-app/secure-connect-killrvideocluster.zip";
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

First, change back into the project root:

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

Check this line:

```java
        // Config loader from file
        DriverConfigLoader loader = DriverConfigLoader.fromFile(
                new File(ConnectivityToAstraWithConfTest.class.getResource("/application_test.conf").getFile()));
        
        // Use it to create the session
        try (CqlSession cqlSession = CqlSession.builder().withConfigLoader(loader).build()) {
            LOGGER.info(" + [OK] - Connection Established to Astra with Keyspace {}", 
                    cqlSession.getKeyspace().get());
        }
```

Here we are loading the config from a file named `application_test.con`
And then we are passing this loaded configuration to the cqlSession.

Locate the `application_test.conf` file in the following resources folder

```
/workspace/Spring-boot-todo-app/spring-boot-todo-app/src/test/resources
```

We now define our cluster connection settings in the file `application_test.conf`:

```
    session-keyspace = todoapp
    cloud {
      secure-connect-bundle = /workspace/Spring-boot-todo-app/spring-boot-todo-app/secure-connect-killrvideocluster.zip
    }
  }

  advanced {
    auth-provider {
      class = PlainTextAuthProvider
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

Don't forget to save the `application_test.conf`, and then we can test the modified connection. Change back into the project root:

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

We are implementing a public interface of the name `TodoAppSchema`. This is located in the model folder of our main app, as we will be using it too later for the full app.

You will find the the interface `TodoAppSchema.java` here:

```
/workspace/Spring-boot-todo-app/spring-boot-todo-app/src/main/java/com/datastax/examples/model
```

It defines the task datamodel that we want to use, with its table name, column names and types:

```java
public interface TodoAppSchema {
  
    /** Constants for table todo_tasks */
    String TABLE_TODO_TASKS     = "todo_tasks";
    String TASK_COL_UID         = "uid";
    String TASK_COL_TITLE       = "title";
    String TASK_COL_COMPLETED   = "completed";
    String TASK_COL_OFFSET      = "offset";
```

We are using the driver's schema builder to build this simple statement:

```java
            // Given a statement
            SimpleStatement stmtCreateTable = SchemaBuilder.createTable(TABLE_TODO_TASKS).ifNotExists()
                    .withPartitionKey(TASK_COL_UID, DataTypes.UUID)
                    .withColumn(TASK_COL_TITLE, DataTypes.TEXT)
                    .withColumn(TASK_COL_COMPLETED, DataTypes.BOOLEAN)
                    .withColumn(TASK_COL_OFFSET, DataTypes.INT)
                    .build();
```

and then we execute this SimpleStatement with the cqlSession:

```java
            cqlSession.execute(stmtCreateTable);
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

Locate the test CRUDWithAstraTest.java

Again, this is in the test source folder.

This one also implements the public interface for Task.

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

To test, change back into the project root and run this command.

```
mvn test -Dtest=com.datastax.examples.CRUDWithAstraTest#test_Insert
```

Check in the Astra cql shell to check if the data is there.

Success!

Now we can move on to implementing our rest controllers with Spring boot.

[🏠 Back to Table of Contents](#table-of-contents)


## Exercise 3 - Building the App ##

Let's move away from the tests and have a look at our Spring application.

For the minimal architecture, we need the model, a repository, a controller and the driver configuration.

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


As before, we are using the following Task model:

```java
public interface TodoAppSchema {
  
    /** Constants for table todo_tasks */
    String TABLE_TODO_TASKS     = "todo_tasks";
    String TASK_COL_UID         = "uid";
    String TASK_COL_TITLE       = "title";
    String TASK_COL_COMPLETED   = "completed";
    String TASK_COL_OFFSET      = "offset";
  
}
```

This is implemented in the class Task.java

Then we need a public interface `TodoListRepository.java`

This defines the API that we will expose:

```
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
```

The class `TodoListRepositoryCassandraDriverImpl.java` is the Cassandra specific implementation of the interface, taking into account the public Task interface that we saw earlier in our tests.

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
    public void upsert(Task dto) {
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
                            content = @Content(schema = @Schema(implementation = Task.class))),
            @ApiResponse(responseCode = "400", description = "Title is blank but is mandatory"),
            @ApiResponse(responseCode = "500", description = "An error occur in storage") })
    @RequestMapping(
            value = "/",
            method = POST,
            produces = APPLICATION_JSON_VALUE,
            consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<Task> create(HttpServletRequest request,
            @RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Only field <b>title</b> is required in the following JSON object", 
                    required = true, 
                    content = @Content(schema = @Schema(implementation = Task.class)))
            Task taskCreationRequest)
    throws URISyntaxException {
        Assert.notNull(taskCreationRequest, "You must provide a Task in BODY");
        Assert.hasLength(taskCreationRequest.getTitle(), "Title is a required field to create a task");
        logger.info("Create new Task at {} with title {}",
                request.getRequestURL().toString(), taskCreationRequest.getTitle());
        Task dto = new Task(UUID.randomUUID(),
                taskCreationRequest.getTitle(),
                taskCreationRequest.isCompleted(),
                taskCreationRequest.getOrder());
        todoRepository.upsert(dto);
        // Created
        return ResponseEntity.ok(dto);
    }
```

Note: some annotations, such as `@Tag`, `@Operation`, `@ApiResponse` are added to support Swagger (which in turn supports our tests).

Before we can run the application, we need to update the `application.conf` in the resources folder for the main app to ensure that the driver has the correct configurations.

Locate the file here:

```
/workspace/Spring-boot-todo-app/spring-boot-todo-app/src/main/resources
```

Update with the location of your secure connect bundle, user name, password, etc. Don't forget to save.

```
datastax-java-driver {

  basic {
    request {
      timeout     = 8 seconds
      consistency = LOCAL_QUORUM
      page-size = 5000
    }
    session-keyspace = todoapp
    cloud {
      secure-connect-bundle = /workspace/Spring-boot-todo-app/spring-boot-todo-app/secure-connect-killrvideocluster.zip
    }
  }

  advanced {
    auth-provider {
      class = PlainTextAuthProvider
      username = KVUser
      password = KVPassword
    }

    connection {
      init-query-timeout = 10 seconds
      set-keyspace-timeout = 10 seconds
    }
    control-connection.timeout = 10 seconds
  }

```

Now run the application:

```
mvn spring-boot:run
```

This is the expected output:

```
2020-09-07 13:42:33.856  INFO 1683 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat initialized with port(s): 8080 (http)
2020-09-07 13:42:33.867  INFO 1683 --- [           main] o.apache.catalina.core.StandardService   : Starting service [Tomcat]
2020-09-07 13:42:33.867  INFO 1683 --- [           main] org.apache.catalina.core.StandardEngine  : Starting Servlet engine: [Apache Tomcat/9.0.35]
2020-09-07 13:42:33.966  INFO 1683 --- [           main] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring embedded WebApplicationContext
2020-09-07 13:42:33.966  INFO 1683 --- [           main] o.s.web.context.ContextLoader            : Root WebApplicationContext: initialization completed in 1181 ms
2020-09-07 13:42:34.928  INFO 1683 --- [           main] c.d.o.d.i.core.DefaultMavenCoordinates   : DataStax Java driver for Apache Cassandra(R) (com.datastax.oss:java-driver-core) version 4.6.1
2020-09-07 13:42:35.425  INFO 1683 --- [     s0-admin-0] c.d.oss.driver.internal.core.time.Clock  : Using native clock for microsecond precision
2020-09-07 13:42:35.691  INFO 1683 --- [        s0-io-0] c.d.oss.driver.api.core.uuid.Uuids       : PID obtained through native call to getpid(): 1683
2020-09-07 13:42:36.570  INFO 1683 --- [           main] o.s.s.concurrent.ThreadPoolTaskExecutor  : Initializing ExecutorService 'applicationTaskExecutor'
2020-09-07 13:42:37.371  INFO 1683 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 8080 (http) with context path ''
2020-09-07 13:42:37.388  INFO 1683 --- [           main] com.datastax.examples.Application        : Started Application in 5.37 seconds (JVM running for 5.917)
```

The API is now available on port 8080.

Gitpod encourages you to open a browser:

<img width="600" alt="Screenshot 2020-09-07 at 16 05 51" src="https://user-images.githubusercontent.com/20337262/92401408-7e1af000-f125-11ea-8854-fd71911b49ea.png">

Note the custom URL that gitpod generates for public access through port 8080, the port is appended at the beginning of the link:

```
https://8080-<your-uuid>.<your-region>.gitpod.io/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config
```

You should see the Swagger UI:

<img width="600" alt="Screenshot 2020-09-07 at 16 09 39" src="https://user-images.githubusercontent.com/20337262/92401468-a571bd00-f125-11ea-8b62-392f42f88f12.png">

Well done! 

[🏠 Back to Table of Contents](#table-of-contents)


## Exercise 4 - Test the API with Swagger ##

Play with Swagger.

To list all todos that are currently in the database:

Select the option `GET /api/v1/todos/ Retrieve the complete list of tasks`

Then opt for `Try it out` and `Execute`.

You will see the used `curl` command (you can also try this in any terminal), and the response from the server.

Try inserting some tasks and compare with output in the Astra cqlsh console. 

Thanks for doing the exercises!

[🏠 Back to Table of Contents](#table-of-contents)


## Bonus Exercise - Test with Todo MVC, client GUI and API specs ##

To test with the Todo MVC client and spec tests, we need to make changes to the data model in order to support the test specs. The model has been simplified for this particular workshop.

Check the instructions in this repository for the details re Todo MVC.

https://github.com/DataStax-Academy/microservices-java-workshop-online







