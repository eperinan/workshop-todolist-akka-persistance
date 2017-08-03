package com.todolist.model

sealed trait Commands

case class CreateTodoListCommand(todoListId: String, name: String) extends Commands
case class DeleteTodoListCommand(todoListId: String) extends Commands
case class AddTaskCommand(todoListId: String, taskId: String, title: String) extends Commands
case class CompleteTaskCommand(todoListId: String, taskId: String, done: Boolean) extends Commands
case class DeleteTaskCommand(todoListId: String, taskId: String) extends Commands

case class StartServer()