package com.todolist.state

import com.todolist.model.ToDoList

case class TodoListState(todo: Map[String, ToDoList])