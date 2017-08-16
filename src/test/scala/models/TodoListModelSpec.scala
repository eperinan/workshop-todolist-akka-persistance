package models

import com.todolist.models._
import com.todolist.serializers.{ EventSerializer, StateSerializer }
import org.scalatest.{ BeforeAndAfterAll, Matchers, WordSpecLike }

class TodoListModelSpec() extends Matchers with WordSpecLike with BeforeAndAfterAll {

  val serializer = new StateSerializer

  def serializer(serializer: EventSerializer, event: Event): Any = {
    val manifest = serializer.manifest(event)
    val binary = serializer.toBinary(event)
    val result = serializer.fromBinary(binary, manifest)
    assert(result == event)
  }

  "TodoListModel" must {

    "TodoList check model in proto" in {

      val task = Task("taskId", "Task", true, 12345)
      val todoList = TodoList("todolistId", "TodoList", Map("taskId" -> task), 123)

      val todoListProto = todoList.toProto
      assert(TodoList(todoListProto) == todoList)
    }

    "TodoListState check model in proto" in {

      val task = Task("taskId", "Task", true, 12345)
      val todoList = TodoList("todolistId", "TodoList", Map("taskId" -> task), 123)
      val todoListState = TodoListState(Map("todolistId" -> todoList))

      val todoListStateProto = todoListState.toProto
      val todoListStateBytes = serializer.toBinary(todoListState)

      assert(TodoListState(todoListStateProto) == todoListState)
      assert(TodoListState(todoListStateBytes) == todoListState)
    }

  }
}

