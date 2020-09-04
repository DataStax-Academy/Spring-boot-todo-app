### Read Me ###

## Exercise 0 - Preparations ##

Start gitpod

```diff 
@@ ONLINE in GITPOD @@ 
```

**✅  Open gitpod** : [Gitpod](http://www.gitpod.io/?utm_source=datastax&utm_medium=referral&utm_campaign=datastaxworkshops) is an IDE 100% online based on Eclipse Theia. To initialize your environment simply click on the button below *(CTRL + Click to open in new tab)*

[![Open in Gitpod](https://gitpod.io/button/open-in-gitpod.svg)](https://gitpod.io/#https://github.com/DataStax-Academy/Spring-boot-todo-app/tree/master/spring-boot-todo-app)

Target url looks like  `https://<your_uid>.<your_region>.gitpod.io/#/workspace/cassandra-workshop-series`. These URL's are dynamic and we cannot provide clickable links in advance. You will need to copy-paste `<your_uid>.<your_region>` as we will insert them in each URL during the exercises.


First step: Upload your Astra secure bundle.

To upload a file to gitpod, you need to select a folder in the left hand explorer panel first. Select the folder todobackend-cassandra

Then select File -> Upload Files... from the header menu and upload the secure bundle.

You should now see it in the todobackend-cassandra folder

```
gitpod /workspace/microservices-java-workshop-online/todobackend-cassandra $ ls
pom.xml  secure-connect-killrvideocluster.zip  src  target
```

Next, install all dependencies, but exclude tests, as we have not implemented them yet:

```
mvn clean install -Dmaven.test.skip=true
```

Your output should end like this:

```
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  8.125 s
[INFO] Finished at: 2020-09-02T09:11:36Z
[INFO] ------------------------------------------------------------------------
```

## Exercise 1 - Test the connection to Astra ##

In the Gitpod explorer, locate the file ConnectivityToAstraExplicitTest.java

You will find it in the following directory:

```
/workspace/microservices-java-workshop-online/todobackend-cassandra/src/test/java/com/datastax/samples/astra
```

Modify the following section with your own credentials.

```
    /** Settings. */
    public static String ASTRA_ZIP_FILE = "/workspace/microservices-java-workshop-online/todobackend-cassandra/secure-connect-killrvideocluster.zip";
    public static String ASTRA_USERNAME = "KVUser";
    public static String ASTRA_PASSWORD = "KVPassword";
    public static String ASTRA_KEYSPACE = "killrvideo";
```

See here how we connect to Astra with the cqlSession:

```
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

```
mvn test -Dtest=com.datastax.examples.ConnectivityToAstraExplicitTest#should_connect_to_Astra_static
```

Yay, we are connected:

```
-------------------------------------------------------
 T E S T S
-------------------------------------------------------
Running com.datastax.examples.ConnectivityToAstraExplicitTest
08:52:20.344 INFO  com.datastax.examples.ConnectivityToAstraExplicitTest :  + [OK] - Connection Established to Astra with Keyspace killrvideo
Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 9.856 sec - in com.datastax.examples.ConnectivityToAstraExplicitTest

Results :

Tests run: 1, Failures: 0, Errors: 0, Skipped: 0

[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  11.685 s
[INFO] Finished at: 2020-09-04T08:52:22+01:00
[INFO] ------------------------------------------------------------------------

```

Now let's configure the driver with the configuration file for the next test:

Locate the file: `ConnectivityToAstraWithConfTest.java` (in the same test source folder)

Check this line:

```
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

Locate the `application_test.conf` file in the resources folder

we need to make our cluster connection changes in this file now:

```
    session-keyspace = killrvideo
    cloud {
      secure-connect-bundle = /workspace/microservices-java-workshop-online/todobackend-cassandra/secure-connect-killrvideocluster.zip
    }
  }
  
  advanced {
    auth-provider {
      class = PlainTextAuthProvider
      username = KVUser 
      password = KVPassword
```

While we are at it, we might as well make a couple more changes that allow for slow connections:

Add this to the advanced

```
    connection {
      init-query-timeout = 10 seconds
      set-keyspace-timeout = 10 seconds
    }
    control-connection.timeout = 10 seconds
```

Don't forget to save the `application_test.conf`, and then we can test the modified connection

```
mvn test -Dtest=com.datastax.examples.ConnectivityToAstraWithConfTest#should_connect_to_Astra_withConfig
```

Success:

```
-------------------------------------------------------
 T E S T S
-------------------------------------------------------
Running com.datastax.examples.ConnectivityToAstraExplicitTest
08:51:29.331 INFO  com.datastax.examples.ConnectivityToAstraExplicitTest :  + [OK] - Connection Established to Astra with Keyspace killrvideo
Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 9.761 sec - in com.datastax.examples.ConnectivityToAstraExplicitTest

Results :

Tests run: 1, Failures: 0, Errors: 0, Skipped: 0

[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  11.650 s
[INFO] Finished at: 2020-09-04T08:51:31+01:00
[INFO] ------------------------------------------------------------------------
```

## Exercice 2 - Create schema and test some inserts

Now that we have configured the driver config file, we can use the session to execute some statements. First we will execute schema statements, in order to create the right schema.

Locate the file `CreateSchemaInAstraTest.java` in the same test source folder.

Inspect the code:

We are implementing a public interface of the name TodoAppSchema. This is located in the model folder here, as we will be using it too later for the full app. 

It defines the task datamodel that we want to use, with its table name, column names and types:

```
public interface TodoAppSchema {
  
    /** Constants for table todo_tasks */
    String TABLE_TODO_TASKS     = "todo_tasks";
    String TASK_COL_UID         = "uid";
    String TASK_COL_TITLE       = "title";
    String TASK_COL_COMPLETED   = "completed";
    String TASK_COL_OFFSET      = "offset";
```

We are using the schema builder to build this simple statement:

```
            // Given a statement
            SimpleStatement stmtCreateTable = SchemaBuilder.createTable(TABLE_TODO_TASKS).ifNotExists()
                    .withPartitionKey(TASK_COL_UID, DataTypes.UUID)
                    .withColumn(TASK_COL_TITLE, DataTypes.TEXT)
                    .withColumn(TASK_COL_COMPLETED, DataTypes.BOOLEAN)
                    .withColumn(TASK_COL_OFFSET, DataTypes.INT)
                    .build();
```

and then we execute this SimpleStatement with the cqlSession:

```
            cqlSession.execute(stmtCreateTable);
```

Let's run this test. This will create the schema in our database.

```
mvn test -Dtest=com.datastax.examples.CreateSchemaInAstraTest#should_create_expected_table
```

Here is the expected output from the test:

```
-------------------------------------------------------
 T E S T S
-------------------------------------------------------
Running com.datastax.examples.CreateSchemaInAstraTest
08:49:36.759 INFO  com.datastax.examples.CreateSchemaInAstraTest : Connection Established to Astra with Keyspace 'killrvideo'
08:49:37.268 INFO  com.datastax.examples.CreateSchemaInAstraTest : Table 'todo_tasks' has been created (if needed).
Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 13.297 sec - in com.datastax.examples.CreateSchemaInAstraTest

Results :

Tests run: 1, Failures: 0, Errors: 0, Skipped: 0

[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  16.243 s
[INFO] Finished at: 2020-09-04T08:49:39+01:00
[INFO] ------------------------------------------------------------------------
```

Let's insert some test data:

Locate the test CrudWithCassandraDriverIntegrationTest.java

Again, this is in the test source folder.

This one also implements the public interface for Task.

To test:

```
mvn test -Dtest=com.datastax.samples.astra.CrudWithCassandraDriverIntegrationTest#test_Insert
```

Check in the Astra cql console to check if the data is there.

Success!

Now we can move onto to implementing our rest controllers with Spring boot.

Move on to the next step.



## Exercise 3 - Rest Controllers ##

Let's move away from the tests and have a look at our Spring application.

For the minimal architecture, we need the model, a repository, a controller and the driver configuration.

Let's look at the repositories first:

First we have the public interface `TodoListRepository.java`

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

The we have the Cassandra specific implementation of the interface, taking into account the public Task interface that we saw earlier in our tests.

This is a big file, so won't copy it here. In a nutshell, it implements each of the interface functions so they can be executed via the driver.
