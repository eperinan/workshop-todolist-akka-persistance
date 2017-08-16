package com.todolist.models

import com.todolist.protobuf.TodoList.{ TodoListStateProto, TaskProto, TodoListProto }

sealed trait State {
  def toProto: Proto
}

case class TodoListState(todo: Map[String, TodoList]) extends State {
  override def toProto: TodoListStateProto = {
    TodoListStateProto(
      todo = todo.values.map(_.toProto).toList
    )
  }
}

object TodoListState {
  def apply(proto: TodoListStateProto): TodoListState = {
    TodoListState(
      todo = proto.todo.map(item => item.id -> TodoList(item.id, item.name, item.tasks.map(task => task.id -> Task(task)) toMap, item.date)) toMap
    )
  }

  def apply(bytes: Array[Byte]): TodoListState = {
    TodoListState(TodoListStateProto.parseFrom(bytes))
  }
}

case class Task(id: String, title: String, done: Boolean, date: Long) extends State {
  override def toProto: TaskProto = {
    TaskProto(id = id, title = title, done = done, date = date)
  }
}

object Task {
  def apply(proto: TaskProto): Task = {
    Task(id = proto.id, title = proto.title, done = proto.done, date = proto.date)
  }
}

case class TodoList(id: String, name: String, tasks: Map[String, Task], date: Long) extends State {
  override def toProto: TodoListProto = {
    TodoListProto(
      id = id,
      name = name,
      tasks = tasks.values.map(_.toProto) toList,
      date = date
    )
  }
}

object TodoList {
  def apply(proto: TodoListProto): TodoList = {
    TodoList(
      id = proto.id,
      name = proto.name,
      tasks = proto.tasks.map(task => task.id -> Task(task)) toMap,
      date = proto.date
    )
  }
}