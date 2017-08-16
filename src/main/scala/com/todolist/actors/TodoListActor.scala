package com.todolist.actors

import akka.actor.ActorLogging
import akka.persistence._
import com.todolist.models._
import scala.util.Failure
import java.time.{ Instant, LocalDateTime }

class TodoListActor extends PersistentActor with ActorLogging {

  val snapShotInterval = 5
  var state = TodoListState(Map[String, TodoList]())

  override def persistenceId = "todo-list-actor"

  private def updateState(event: Event): Unit = {
    event match {
      case TodoListCreatedEvent(todoListId, name, date) => {
        state = TodoListState(state.todo + (todoListId -> TodoList(todoListId, name, Map(), date)))
      }
      case TodoListDeletedEvent(todoListId: String) => {
        state = TodoListState(state.todo - todoListId)
      }
      case TaskAddedEvent(todoListId, taskId, title, date) => {
        val tasksUpdated = state.todo(todoListId).tasks + (taskId -> Task(taskId, title, false, date))
        val todoListUpdated = TodoList(todoListId, state.todo(todoListId).name, tasksUpdated, date)
        state = TodoListState(state.todo updated (todoListId, todoListUpdated))
      }
      case TaskDeletedEvent(todoListId, taskId) => {
        val tasksUpdated = state.todo(todoListId).tasks - taskId
        val todoListUpdated = TodoList(todoListId, state.todo(todoListId).name, tasksUpdated, state.todo(todoListId).date)
        state = TodoListState(state.todo updated (todoListId, todoListUpdated))
      }
      case TaskCompletedEvent(todoListId, taskId, done) => {
        val tasksUpdated = state.todo(todoListId).tasks updated (taskId, Task(taskId, state.todo(todoListId).tasks(taskId).title, done, state.todo(todoListId).date))
        val todoListUpdated = TodoList(todoListId, state.todo(todoListId).name, tasksUpdated, state.todo(todoListId).date)
        state = TodoListState(state.todo updated (todoListId, todoListUpdated))
      }
      case _ => log.info(s"Updated state")
    }
  }

  override def preStart() = {
    log.info(s"${self.path} Started")
  }

  override def preRestart(reason: Throwable, message: Option[Any]) {
    log.error(reason, s"${self.path} Restarting due to [{}] when processing [{}]", reason.getMessage, message.getOrElse(""))
  }

  val receiveRecover: Receive = {
    case evt: Event => updateState(evt)
    case SnapshotOffer(_, snapshot: TodoListState) => {
      state = snapshot
      log.info(s"${self.path} Notification snapshot restore")
    }
    case RecoveryCompleted => {
      log.info(s"${self.path} Notification recovery complete")
    }
  }

  override def persist[A](event: A)(handler: A ⇒ Unit): Unit = {
    if (lastSequenceNr % snapShotInterval == 0 && lastSequenceNr != 0) {
      saveSnapshot(state)
      log.info(s"${self.path} Snapshot saved ${lastSequenceNr}")
    }
    super.persist(event)(handler)
  }

  def receiveCommand = {
    case GetToDoListsRequests() => {
      log.info(s"${self.path} Returning todolist")
      sender ! TodoListStateResponse(state.todo)
    }
    case GetToDoListsByIdRequests(todoListId) => {
      log.info(s"${self.path} Returning todolist ${todoListId} ...")
      if (state.todo.contains(todoListId)) {
        val todoList = state.todo(todoListId)
        sender ! TodoListResponse(todoList.id, todoList.name, todoList.tasks)
      } else {
        sender ! Failure(new IllegalArgumentException("Todolist not found"))
      }
    }
    case SaveSnapshotSuccess(metadata) => {
      log.info(s"${self.path} Notification save snapshot success")
    }
    case SaveSnapshotFailure(metadata, reason) => {
      log.info(s"${self.path} Notification save snapshot failure")
    }
    case CreateTodoListCommand(name: String) => {
      val todoListId = java.util.UUID.randomUUID.toString
      val date = Instant.now().toEpochMilli()
      if (state.todo.contains(todoListId)) {
        // Notification
        val message = s"${self.path} Notification ${todoListId} already exist"
        log.info(message)
        sender ! Failure(new IllegalArgumentException(message))
      } else {
        val event = TodoListCreatedEvent(todoListId, name, date)
        persist(event) { evt =>
          updateState(evt)
          // We should publish event but in this training we are not going to do it
          log.info(s"${self.path} Created ${todoListId} Todo List")
          sender() ! evt
        }
      }
    }
    case DeleteTodoListCommand(todoListId: String) => {
      if (state.todo.contains(todoListId)) {
        val event = TodoListDeletedEvent(todoListId)
        persist(event) { evt =>
          updateState(evt)
          // We should publish event but in this training we are not going to do it
          log.info(s"${self.path} Removed ${todoListId} Todo List")
          sender() ! evt
        }
      } else {
        // Notification
        val message = s"${self.path} Notification ${todoListId} doesn´t exist"
        log.info(message)
        sender ! Failure(new IllegalArgumentException(message))
      }
    }
    case AddTaskCommand(todoListId: String, title: String) => {
      if (state.todo.contains(todoListId)) {
        val taskId = java.util.UUID.randomUUID.toString
        val date = Instant.now().toEpochMilli()
        if (state.todo(todoListId).tasks.contains(taskId)) {
          log.info(s"${self.path} Notification the Todo List ${todoListId} has a taskId with this id ${taskId}")
        } else {
          val event = TaskAddedEvent(todoListId, taskId, title, date)
          persist(event) { evt =>
            updateState(evt)
            // We should publish event but in this training we are not going to do it
            log.info(s"${self.path} Added task ${taskId} to ${todoListId} Todo List")
            sender() ! evt
          }
        }
      } else {
        // Notification
        val message = s"${self.path} Notification the Todo List ${todoListId} doesn´t exist"
        log.info(message)
        sender ! Failure(new IllegalArgumentException(message))
      }
    }
    case DeleteTaskCommand(todoListId: String, taskId: String) => {
      if (state.todo.contains(todoListId)) {
        if (state.todo(todoListId).tasks.contains(taskId)) {
          val event = TaskDeletedEvent(todoListId, taskId)
          persist(event) { evt =>
            updateState(evt)
            // We should publish event but in this training we are not going to do it
            log.info(s"${self.path} Removed task ${taskId} from ${todoListId} Todo List")
            sender ! evt
          }
        } else {
          val message = s"${self.path} Notification the Todo List ${todoListId} doesn´ have a taskId with this id ${taskId}"
          log.info(message)
          sender ! Failure(new IllegalArgumentException(message))

        }
      } else {
        // Notification
        val message = s"${self.path} Notification the Todo List ${todoListId} doesn´t exist"
        log.info(message)
        sender ! Failure(new IllegalArgumentException(message))
      }
    }
    case CompleteTaskCommand(todoListId: String, taskId: String, done: Boolean) => {
      if (state.todo.contains(todoListId)) {
        if (state.todo(todoListId).tasks.contains(taskId)) {
          val event = TaskCompletedEvent(todoListId, taskId, done)
          persist(event) { evt =>
            updateState(evt)
            // We should publish event but in this training we are not going to do it
            log.info(s"${self.path} Update status of the task ${taskId} from ${todoListId} Todo List")
            sender ! evt
          }
        } else {
          val message = s"${self.path} Notification the Todo List ${todoListId} doesn´ have a taskId with this id ${taskId}"
          log.info(message)
          sender ! Failure(new IllegalArgumentException(message))
        }
      } else {
        // Notification
        val message = s"${self.path} Notification the Todo List ${todoListId} doesn´t exist"
        log.info(message)
        sender ! Failure(new IllegalArgumentException(message))
      }
    }
    case _ => {
      log.info(s"${self.path} Unknow message")
    }
  }
}
