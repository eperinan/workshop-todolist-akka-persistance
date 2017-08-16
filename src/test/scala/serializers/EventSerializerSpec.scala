package serializers

import com.todolist.models._
import com.todolist.serializers.EventSerializer
import org.scalatest.{ BeforeAndAfterAll, Matchers, WordSpecLike }

class EventSerializerSpec() extends Matchers with WordSpecLike with BeforeAndAfterAll {

  val serializer = new EventSerializer

  def serializer(serializer: EventSerializer, event: Event): Any = {
    val manifest = serializer.manifest(event)
    val binary = serializer.toBinary(event)
    val result = serializer.fromBinary(binary, manifest)
    assert(result == event)
  }

  "EventSerializer" must {

    "todoListCreatedEvent" in {
      val event = TodoListCreatedEvent("todoListId", "TodoList", 12345)
      serializer(serializer, event)
    }

    "ToDoListDeletedEventManifest" in {
      val event = TodoListDeletedEvent("todoListId")
      serializer(serializer, event)
    }

    "TaskAddedEvent" in {
      val event = TaskAddedEvent("todoListId", "taskId", "title", 12345)
      serializer(serializer, event)
    }

    "TaskCompletedEvent" in {
      val event = TaskCompletedEvent("todoListId", "taskId", false)
      serializer(serializer, event)
    }

    "TaskDeletedEvent" in {
      val event = TaskDeletedEvent("todoListId", "taskId")
      serializer(serializer, event)
    }
  }
}

