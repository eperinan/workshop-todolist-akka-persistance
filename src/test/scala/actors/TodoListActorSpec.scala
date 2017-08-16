package actors

import akka.actor.{ ActorSystem, Props }
import akka.testkit.{ ImplicitSender, TestKit }
import com.todolist.actors.TodoListActor
import com.todolist.models._
import com.todolist.test.PersistenceSuite
import org.scalatest.{ BeforeAndAfterAll, Matchers, WordSpecLike }

import scala.concurrent.duration._

class TodoListActorSpec() extends TestKit(ActorSystem("MySpec", PersistenceSuite.config)) with ImplicitSender
    with WordSpecLike with Matchers with BeforeAndAfterAll {

  val myTimeout = 3 seconds

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  "ATodoListActor" must {

    "Empty todo list" in {
      val todoListActor = system.actorOf(Props[TodoListActor])
      todoListActor ! GetToDoListsRequests()
      val todolist = expectMsgPF(myTimeout)({
        case state: TodoListStateResponse =>
          assert(state.todo == Map())
          state
      })
    }

    "Create todoList" in {
      val myName = "Test todo list"
      val todoListActor = system.actorOf(Props[TodoListActor])
      todoListActor ! CreateTodoListCommand(myName)
      expectMsgPF(myTimeout)({
        case evt: TodoListCreatedEvent =>
          assert(evt.name == myName)
          evt
      })
    }

    "Add task to todolist" in {
      val myName = "Test todo list to create Task"
      val myTitle = "Title task"
      val todoListActor = system.actorOf(Props[TodoListActor])
      todoListActor ! CreateTodoListCommand(myName)
      val todoListId = expectMsgPF(myTimeout)({
        case evt: TodoListCreatedEvent =>
          assert(evt.name == myName)
          evt.todoListId
      })
      todoListActor ! AddTaskCommand(todoListId, myTitle)
      expectMsgPF(myTimeout)({
        case evt: TaskAddedEvent =>
          assert(evt.title == myTitle)
          evt
      })
    }

    "Delete task from todolist" in {
      val myName = "Test todo list to create Task"
      val myTitle = "Title task"
      val todoListActor = system.actorOf(Props[TodoListActor])
      todoListActor ! CreateTodoListCommand(myName)
      val todoListId = expectMsgPF(myTimeout)({
        case evt: TodoListCreatedEvent =>
          assert(evt.name == myName)
          evt.todoListId
      })
      todoListActor ! AddTaskCommand(todoListId, myTitle)
      val taskId = expectMsgPF(myTimeout)({
        case evt: TaskAddedEvent =>
          assert(evt.title == myTitle)
          evt.taskId
      })

      todoListActor ! DeleteTaskCommand(todoListId, taskId)
      expectMsgPF(myTimeout)({
        case evt: TaskDeletedEvent =>
          assert(evt.todoListId == todoListId)
          assert(evt.taskId == taskId)
          evt
      })
    }

    "Complete task from todolist" in {
      val myName = "Test todo list to create Task"
      val myTitle = "Title task"
      val todoListActor = system.actorOf(Props[TodoListActor])
      todoListActor ! CreateTodoListCommand(myName)
      val todoListId = expectMsgPF(myTimeout)({
        case evt: TodoListCreatedEvent =>
          assert(evt.name == myName)
          evt.todoListId
      })
      todoListActor ! AddTaskCommand(todoListId, myTitle)
      val taskId = expectMsgPF(myTimeout)({
        case evt: TaskAddedEvent =>
          assert(evt.title == myTitle)
          evt.taskId
      })

      val status = true

      todoListActor ! CompleteTaskCommand(todoListId, taskId, status)
      expectMsgPF(myTimeout)({
        case evt: TaskCompletedEvent =>
          assert(evt.todoListId == todoListId)
          assert(evt.taskId == taskId)
          assert(evt.done == status)
          evt
      })
    }

    "Get todolist by Id" in {
      val myName = "Test todo list to create Task"
      val todoListActor = system.actorOf(Props[TodoListActor])
      todoListActor ! CreateTodoListCommand(myName)
      val todoListId = expectMsgPF(myTimeout)({
        case evt: TodoListCreatedEvent =>
          assert(evt.name == myName)
          evt.todoListId
      })
      todoListActor ! GetToDoListsByIdRequests(todoListId)
      expectMsgPF(myTimeout)({
        case evt: TodoListResponse =>
          assert(evt.name == myName)
          evt
      })
    }

    "Delete todolist by Id" in {
      val myName = "Test todo list to create Task"
      val todoListActor = system.actorOf(Props[TodoListActor])
      todoListActor ! CreateTodoListCommand(myName)
      val todoListId = expectMsgPF(myTimeout)({
        case evt: TodoListCreatedEvent =>
          assert(evt.name == myName)
          evt.todoListId
      })
      todoListActor ! DeleteTodoListCommand(todoListId)

      expectMsgPF(myTimeout)({
        case evt: TodoListDeletedEvent =>
          assert(evt.todoListId == todoListId)
          evt
      })
    }

  }
}