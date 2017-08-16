
import com.todolist.models.{ Task, TodoList, TodoListState }
import com.todolist.serializers.StateSerializer
import org.scalatest.{ FlatSpec, Matchers }

class StateSerializerSpec extends FlatSpec with Matchers {

  val task = Task("taskId", "Task", true, 12345)
  val todoList = TodoList("todolistId", "TodoList", Map("taskId" -> task), 123)
  val todoListState = TodoListState(Map("todolistId" -> todoList))
  val serializer = new StateSerializer

  "StateSerializer" should "serialize TodoListState" in {
    val manifest = serializer.manifest(todoListState)
    val binary = serializer.toBinary(todoListState)
    val result = serializer.fromBinary(binary, manifest)
    assert(result == todoListState)
  }

}