package com.todolist

import akka.actor.{ ActorSystem, Props }
import akka.http.scaladsl.server.Directives
import akka.stream.ActorMaterializer
import com.todolist.actors.{ ServerHttpActor, TodoListActor }
import com.todolist.model._

object Boot extends Directives {
  def main(args: Array[String]) {

    val system = ActorSystem("todolist-akka-training")

    // Create actors
    val serverHttpActor = system.actorOf(Props[ServerHttpActor], "serverHttp")
    val todoListActor = system.actorOf(Props[TodoListActor], "todoList")

    serverHttpActor ! StartServer

    // Commands
    todoListActor ! CreateTodoListCommand("Test", "Test todo list")
    todoListActor ! CreateTodoListCommand("Test2", "Test todo list")
    todoListActor ! DeleteTodoListCommand("Test2")
    todoListActor ! CreateTodoListCommand("Test2", "Test todo list")
    todoListActor ! AddTaskCommand("Test", "Task1", "Buy bread")
    todoListActor ! AddTaskCommand("Test", "Task2", "Buy bread 2")
    todoListActor ! DeleteTaskCommand("Test", "Task1")
    todoListActor ! CompleteTaskCommand("Test", "Task2", true)
  }
}
