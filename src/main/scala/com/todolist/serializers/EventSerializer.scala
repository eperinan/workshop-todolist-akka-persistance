package com.todolist.serializers

import akka.serialization.SerializerWithStringManifest
import com.todolist.models._

class EventSerializer extends SerializerWithStringManifest {

  final val ToDoListCreatedEventManifest: String = classOf[TodoListCreatedEvent].getName
  final val ToDoListDeletedEventManifest: String = classOf[TodoListDeletedEvent].getName
  final val TaskAddedEventManifest: String = classOf[TaskAddedEvent].getName
  final val TaskCompletedEventManifest: String = classOf[TaskCompletedEvent].getName
  final val TaskDeletedEventManifest: String = classOf[TaskDeletedEvent].getName

  override def identifier = 1234567

  def manifest(obj: AnyRef): String =
    obj match {
      case event: TodoListCreatedEvent => event.getClass.getName
      case event: TodoListDeletedEvent => event.getClass.getName
      case event: TaskAddedEvent => event.getClass.getName
      case event: TaskCompletedEvent => event.getClass.getName
      case event: TaskDeletedEvent => event.getClass.getName
    }

  def toBinary(obj: AnyRef): Array[Byte] = {
    obj match {
      case event: Event => event.toProto.toByteArray
    }
  }

  def fromBinary(bytes: Array[Byte], manifest: String): AnyRef = {
    manifest match {
      case ToDoListCreatedEventManifest => TodoListCreatedEvent(bytes)
      case ToDoListDeletedEventManifest => TodoListDeletedEvent(bytes)
      case TaskAddedEventManifest => TaskAddedEvent(bytes)
      case TaskCompletedEventManifest => TaskCompletedEvent(bytes)
      case TaskDeletedEventManifest => TaskDeletedEvent(bytes)
    }
  }
}
