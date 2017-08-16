package com.todolist.services

import akka.actor.ActorRef
import com.todolist.models._
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.{ ExecutionContext, Future }

case class TodoListService(todoListActor: ActorRef)(implicit timeout: Timeout, ec: ExecutionContext) {

  def GetTodoList(): Future[TodoListStateResponse] = todoListActor ? GetToDoListsRequests() map { case r: TodoListStateResponse => r }
  def GetToDoListsById(id: String): Future[TodoListResponse] = todoListActor ? GetToDoListsByIdRequests(id) map { case r: TodoListResponse => r }
  def DeleteToDoListsById(id: String): Future[TodoListDeletedEvent] = (todoListActor ? DeleteTodoListCommand(id)).map { case r: TodoListDeletedEvent => r }
  def DeleteTaskById(todoListId: String, taskId: String): Future[TaskDeletedEvent] = (todoListActor ? DeleteTaskCommand(todoListId, taskId)).map { case r: TaskDeletedEvent => r }
  def CompleteTaskById(todoListId: String, taskId: String, status: Boolean): Future[TaskCompletedEvent] = (todoListActor ? CompleteTaskCommand(todoListId, taskId, status)).map { case r: TaskCompletedEvent => r }
  def CreateTodoList(name: String): Future[TodoListCreatedEvent] = (todoListActor ? CreateTodoListCommand(name)).map { case r: TodoListCreatedEvent => r }
  def AddTaskTodoList(todoListId: String, title: String): Future[TaskAddedEvent] = (todoListActor ? AddTaskCommand(todoListId, title)).map { case r: TaskAddedEvent => r }

}
