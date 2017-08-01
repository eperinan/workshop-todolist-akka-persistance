# TODO List Akka Training

This training is focused in three areas:

1. **Akka Persistent**: We will persist our actors with Protocol Buffer serialization in order to support schema evolution
2. **EventSourcing**: We will create a microservice using EventSourcing and Akka Actors. The microservice will receive **Commands** (actions) and persist **Events** (side-effects)
3. **API Rest**: Simple API in Akka HTTP where we can add, update and remove data using our microservice

## Exercise

In this training we will create a TODO List App. This app could be able to store several lists. Every list will have a name and another list with the tasks to do.

That should be our models:

1. Task (id: String, title: String, done: Boolean)
2. ToDoList (id: String, name: String, tasks: List[Task]) or ToDoList (id: String, name: String, tasks: Map[String, Task])

We have to create a microservive with EventSourcing using Akka Actors in order to create and remove TODO List to the system and add, remove and complete tasks to every list.

## Steps

### Preparing the environment

We will use the following libraries:

- Akka 2.4.10
- For this example is not necessary to persist data on database, for this reason, we are gonna use LevelDB plugin (0.9 version) for persisting data
- ScalaPB library for Protocol Buffer

### Create models and Actor for ToDoList

In this second step, we are going to focus in generate the commands, events and first approach for the `ToDoList` actor.

As we are following the EventSourcing pattern, we are going to do follow the next steps.

Firstly, we are going to send commands to our actor. 
Secondly, the actor will process that command and it will create an event.
Third, the actor will store that event in the internal state.

Normally after store that event and update the internal state, we have to persist the event and publish it to another microservices. The persistence will be addressed in the fourth step. However, in this training, we are not going to treat the publishment because we only are going to focus in the persistance.

To achieve this goal, we will have to create the following commands and events creating an ADT for them:

Commands: 
* CreateTodoListCommand
* DeleteTodoListCommand
* AddTaskCommand
* CompleteTaskCommand
* DeleteTaskCommand

Events:
* TodoListCreatedEvent
* TodoListDeletedEvent
* TaskAddedEvent
* TaskCompletedEvent
* TaskDeletedEvent

Furthermore, we will have to create the case class related with the model exposed previously. As we should know, each actor has own internal state where we will store our `TodoList`. 
Consequently, we will have to create another case class for it.

Considerations:

* We will have to create a private function in the actor whose will manage the update of the state. This function will receive an `Event` defined previously and will return `Unit`.
* We will have to handle the possible errors that we can encountered when we try to access to a non-existent todo list for example. When we encounter one error, we will log that error and we will continue with our execution. 

### Add protobuf messages and serializers

We have to create the protobuf messages and serializers for events and states.

### Persistence and Snapshots

Change our actor in order to allow persitence using Akka Persitence and configure the snapshot for improving the recovery of the actor.

### Rest API

Create a new Actor for the Rest API using Akka HTTP. We have to create one endpoint for every action that we want to implement. We should use Ask Pattern for receiving the responses from the the actor. For that, we should create Request and Responses for every endpoint

### New field in our models

We are gonna create a new field called **date** in **Task** and **ToDoList** model in order to save the date when the user created the ToDoList and the Task

### Testing

Create tests for protobuf conversions and actors

## API

### GET /todolist

Return all the ToDoLists

### POST /todolist

```json
{
  "name": ""
}
```

Create a new ToDoList and return the new ToDoList

### DELETE /todolist/{id-list}

Delete a ToDoList by id

### GET /todolist/{id-list}

Return a ToDoList by id

### POST /todolist/{id-list}/task

```json
{
  "title": ""
}
```

Create a new task in a ToDoList by id and return the new Task

### PUT /todolist/{id-list}/task/{id-task}

```json
{
  "done": true|false
}
```

Update if the task has been completed

### DELETE /todolist/{id-list}/task/{id-task}

Delete a Task by id in a ToDoList

## Follow each step

We have included **sbt-groll** plugin so you can move around the Git commit history and see the progress from configuration to finally run it.

From the sbt console you can use:

```
> groll next
```

The will take you to next commit and step. Use groll prev if you want to go back. [More options here](https://github.com/sbt/sbt-groll#argumentsoptions)

## License

Copyright (C) 2017 47 Degrees, LLC http://47deg.com hello@47deg.com