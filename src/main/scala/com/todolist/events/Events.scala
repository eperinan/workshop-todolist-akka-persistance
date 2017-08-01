package com.todolist.events

sealed trait Event

case class TodoListCreatedEvent(todoListId: String, name: String) extends Event
case class TodoListDeletedEvent(todoListId: String) extends Event
case class TaskAddedEvent(todoListId: String, taskId: String, title: String) extends Event
case class TaskCompletedEvent(todoListId: String, taskId: String, done: Boolean) extends Event
case class TaskDeletedEvent(todoListId: String, taskId: String) extends Event