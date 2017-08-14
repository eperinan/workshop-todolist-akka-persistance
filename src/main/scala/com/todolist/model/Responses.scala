package com.todolist.model

sealed trait Response

case class TodoListStateResponse(todo: Map[String, TodoList]) extends Response
case class TodoListResponse(id: String, name: String, tasks: Map[String, Task]) extends Response
