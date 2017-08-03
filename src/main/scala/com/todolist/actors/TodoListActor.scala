package com.todolist.actors

import akka.actor.ActorLogging
import akka.persistence._
import com.todolist.model._

class TodoListActor extends PersistentActor with ActorLogging {

  val snapShotInterval = 5
  var state = TodoListState(Map[String, TodoList]())

  override def persistenceId = "todo-list-actor"

  private def updateState(event: Event): Unit = {
    event match {
      case TodoListCreatedEvent(todoListId: String, name: String) => {
        state = TodoListState(state.todo + (todoListId -> TodoList(todoListId, name, Map())))
      }
      case TodoListDeletedEvent(todoListId: String) => {
        state = TodoListState(state.todo - todoListId)
      }
      case TaskAddedEvent(todoListId, taskId, title) => {
        val tasksUpdated = state.todo(todoListId).tasks + (taskId -> Task(taskId, title, false))
        val todoListUpdated = TodoList(todoListId, state.todo(todoListId).name, tasksUpdated)
        state = TodoListState(state.todo updated (todoListId, todoListUpdated))
      }
      case TaskDeletedEvent(todoListId, taskId) => {
        val tasksUpdated = state.todo(todoListId).tasks - taskId
        val todoListUpdated = TodoList(todoListId, state.todo(todoListId).name, tasksUpdated)
        state = TodoListState(state.todo updated (todoListId, todoListUpdated))
      }
      case TaskCompletedEvent(todoListId, taskId, done) => {
        val tasksUpdated = state.todo(todoListId).tasks updated (taskId, Task(taskId, state.todo(todoListId).tasks(taskId).title, done))
        val todoListUpdated = TodoList(todoListId, state.todo(todoListId).name, tasksUpdated)
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
    case SaveSnapshotSuccess(metadata) => {
      log.info(s"${self.path} Notification save snapshot success")
    }
    case SaveSnapshotFailure(metadata, reason) => {
      log.info(s"${self.path} Notification save snapshot failure")
    }
    case CreateTodoListCommand(todoListId: String, name: String) => {
      if (state.todo.contains(todoListId)) {
        // Notification
        log.info(s"${self.path} Notification ${todoListId} already exist")
      } else {
        val event = TodoListCreatedEvent(todoListId, name)
        persist(event) { evt =>
          updateState(evt)
          // We should publish event but in this training we are not going to do it
          log.info(s"${self.path} Created ${todoListId} Todo List")
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
        }
      } else {
        // Notification
        log.info(s"${self.path} Notification ${todoListId} doesn´t exist")
      }
    }
    case AddTaskCommand(todoListId: String, taskId: String, title: String) => {
      if (state.todo.contains(todoListId)) {
        if (state.todo(todoListId).tasks.contains(taskId)) {
          log.info(s"${self.path} Notification the Todo List ${todoListId} has a taskId with this id ${taskId}")
        } else {
          val event = TaskAddedEvent(todoListId, taskId, title)
          persist(event) { evt =>
            updateState(evt)
            // We should publish event but in this training we are not going to do it
            log.info(s"${self.path} Added task ${taskId} to ${todoListId} Todo List")
          }
        }
      } else {
        // Notification
        log.info(s"${self.path} Notification the Todo List ${todoListId} doesn´t exist")
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
          }
        } else {
          log.info(s"${self.path} Notification the Todo List ${todoListId} doesn´ have a taskId with this id ${taskId}")
        }
      } else {
        // Notification
        log.info(s"${self.path} Notification the Todo List ${todoListId} doesn´t exist")
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
          }
        } else {
          log.info(s"${self.path} Notification the Todo List ${todoListId} doesn´ have a taskId with this id ${taskId}")
        }
      } else {
        // Notification
        log.info(s"${self.path} Notification the Todo List ${todoListId} doesn´t exist")
      }
    }
    case _ => {
      log.info(s"${self.path} Unknow message")
    }
  }
}
