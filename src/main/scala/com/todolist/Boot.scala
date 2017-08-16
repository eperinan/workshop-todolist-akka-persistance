package com.todolist

import akka.actor.{ ActorSystem, Props }
import akka.http.scaladsl.server.Directives
import com.todolist.actors.{ ServerHttpActor, TodoListActor }
import com.todolist.models._

object Boot extends Directives {
  def main(args: Array[String]) {

    val system = ActorSystem("todolist-akka-training")

    // Create actors
    val serverHttpActor = system.actorOf(Props[ServerHttpActor], "serverHttp")
    val todoListActor = system.actorOf(Props[TodoListActor], "todoList")

    serverHttpActor ! StartServer(todoListActor)

    // Commands
    todoListActor ! CreateTodoListCommand("Test todo list")
    todoListActor ! CreateTodoListCommand("Test todo list second command")
    todoListActor ! DeleteTodoListCommand("Test2")
    todoListActor ! CreateTodoListCommand("Test todo list third command")
    todoListActor ! AddTaskCommand("a3e9c0cc-8c0c-4079-9d8d-a0fab20605f6", "Buy bread")
    todoListActor ! AddTaskCommand("a3e9c0cc-8c0c-4079-9d8d-a0fab20605f6", "Buy bread 2")
    todoListActor ! DeleteTaskCommand("a3e9c0cc-8c0c-4079-9d8d-a0fab20605f6", "Task1")
    todoListActor ! CompleteTaskCommand("a3e9c0cc-8c0c-4079-9d8d-a0fab20605f6", "Task2", true)

  }
}
