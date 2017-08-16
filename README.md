# TodoList Akka Training

Repository with my solution for a private training of 47deg related with Akka persistent, EventSourcing and API Rest.

In this training I am using Scala, AKKA (http, actor, persistent), Circe, ProtocolBuffer and LevelDB.

To run the application:

```
sbt run
```

To run the test and coverage:

```
sbt clean test coverage coverageReport
```

Note: Included files of Postman in the path `resources/postman/todoListAkkaTraning.postman_collection` to check the diferents routes of the API.