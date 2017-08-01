package com.todolist.model

case class Task(id: String, title: String, done: Boolean)
case class ToDoList(id: String, name: String, tasks: Map[String, Task])
