package com.todolist.models

import com.todolist.protobuf.TodoList._

sealed trait Event {
  def toProto: Proto
}

case class TodoListCreatedEvent(todoListId: String, name: String, date: Long) extends Event {
  override def toProto: Proto = {
    TodoListCreatedEventProto(
      todoListId = todoListId,
      name = name,
      date = date
    )
  }
}

object TodoListCreatedEvent {
  def apply(proto: TodoListCreatedEventProto): TodoListCreatedEvent = {
    TodoListCreatedEvent(todoListId = proto.todoListId, name = proto.name, date = proto.date)
  }

  def apply(bytes: Array[Byte]): TodoListCreatedEvent = {
    TodoListCreatedEvent(TodoListCreatedEventProto.parseFrom(bytes))
  }
}

case class TodoListDeletedEvent(todoListId: String) extends Event {
  override def toProto: Proto = {
    TodoListDeletedEventProto(
      todoListId = todoListId
    )
  }
}

object TodoListDeletedEvent {
  def apply(proto: TodoListDeletedEventProto): TodoListDeletedEvent = {
    TodoListDeletedEvent(todoListId = proto.todoListId)
  }

  def apply(bytes: Array[Byte]): TodoListDeletedEvent = {
    TodoListDeletedEvent(TodoListDeletedEventProto.parseFrom(bytes))
  }
}

case class TaskAddedEvent(todoListId: String, taskId: String, title: String, date: Long) extends Event {
  override def toProto: Proto = {
    TaskAddedEventProto(
      todoListId = todoListId,
      taskId = taskId,
      title = title,
      date = date
    )
  }
}

object TaskAddedEvent {
  def apply(proto: TaskAddedEventProto): TaskAddedEvent = {
    TaskAddedEvent(todoListId = proto.todoListId, taskId = proto.taskId, title = proto.title, date = proto.date)
  }

  def apply(bytes: Array[Byte]): TaskAddedEvent = {
    TaskAddedEvent(TaskAddedEventProto.parseFrom(bytes))
  }
}

case class TaskCompletedEvent(todoListId: String, taskId: String, done: Boolean) extends Event {
  override def toProto: Proto = {
    TaskCompletedEventProto(
      todoListId = todoListId,
      taskId = taskId,
      done = done
    )
  }
}

object TaskCompletedEvent {
  def apply(proto: TaskCompletedEventProto): TaskCompletedEvent = {
    TaskCompletedEvent(todoListId = proto.todoListId, taskId = proto.taskId, done = proto.done)
  }

  def apply(bytes: Array[Byte]): TaskCompletedEvent = {
    TaskCompletedEvent(TaskCompletedEventProto.parseFrom(bytes))
  }
}

case class TaskDeletedEvent(todoListId: String, taskId: String) extends Event {
  override def toProto: Proto = {
    TaskDeletedEventProto(
      todoListId = todoListId,
      taskId = taskId
    )
  }
}

object TaskDeletedEvent {
  def apply(proto: TaskDeletedEventProto): TaskDeletedEvent = {
    TaskDeletedEvent(todoListId = proto.todoListId, taskId = proto.taskId)
  }

  def apply(bytes: Array[Byte]): TaskDeletedEvent = {
    TaskDeletedEvent(TaskDeletedEventProto.parseFrom(bytes))
  }
}