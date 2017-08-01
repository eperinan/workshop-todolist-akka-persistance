package com.todolist

import java.util.concurrent.TimeUnit

import akka.actor.{ ActorSystem, Props }
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives
import akka.stream.ActorMaterializer
import akka.util.Timeout

import scala.concurrent.Await
import scala.concurrent.duration._
import com.typesafe.config.ConfigFactory
import com.todolist.actors.TodoListActor
import com.todolist.commands._
import com.todolist.routes.BaseRoutes

object Boot extends Directives {
  def main(args: Array[String]) {

    val config = ConfigFactory.load()

    implicit val system = ActorSystem("todolist-akka-training")
    implicit val materializer = ActorMaterializer()
    implicit val executionContext = system.dispatcher
    implicit val timeout = Timeout(5, TimeUnit.SECONDS)

    // Create actors
    val todoListActor = system.actorOf(Props[TodoListActor], "todoList")

    // Commands
    todoListActor ! CreateTodoListCommand("Test", "Test todo list")
    todoListActor ! CreateTodoListCommand("Test2", "Test todo list")
    todoListActor ! DeleteTodoListCommand("Test2")
    todoListActor ! CreateTodoListCommand("Test2", "Test todo list")
    todoListActor ! AddTaskCommand("Test", "Task1", "Buy bread")
    todoListActor ! AddTaskCommand("Test", "Task2", "Buy bread 2")
    todoListActor ! DeleteTaskCommand("Test", "Task1")
    todoListActor ! CompleteTaskCommand("Test", "Task2", true)

    // Routes
    val routes = BaseRoutes.routes
    val bindingFuture = Http().bindAndHandle(routes, config.getString("todolist-akka-training.hostname"), config.getInt("todolist-akka-training.port"))

    Await.ready(system.whenTerminated, Duration.Inf)

  }
}
