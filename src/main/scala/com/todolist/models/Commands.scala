package com.todolist.models

import akka.actor.ActorRef

sealed trait Commands

case class CreateTodoListCommand(name: String) extends Commands
case class DeleteTodoListCommand(todoListId: String) extends Commands
case class AddTaskCommand(todoListId: String, title: String) extends Commands
case class CompleteTaskCommand(todoListId: String, taskId: String, done: Boolean) extends Commands
case class DeleteTaskCommand(todoListId: String, taskId: String) extends Commands

case class StartServer(todoListActor: ActorRef)