package com.todolist.serializers

import akka.serialization.SerializerWithStringManifest
import com.todolist.models.TodoListState

class StateSerializer extends SerializerWithStringManifest {

  final val TodoListStateManifest: String = classOf[TodoListState].getName

  override def identifier = 2345678

  def manifest(obj: AnyRef): String =
    obj match {
      case state: TodoListState => state.getClass.getName
    }

  def toBinary(obj: AnyRef): Array[Byte] = {
    obj match {
      case state: TodoListState => state.toProto.toByteArray
    }
  }

  def fromBinary(bytes: Array[Byte], manifest: String): AnyRef = {
    manifest match {
      case TodoListStateManifest => TodoListState(bytes)
    }
  }
}