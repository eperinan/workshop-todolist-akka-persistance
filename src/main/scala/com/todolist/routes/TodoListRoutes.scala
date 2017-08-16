package com.todolist.routes

import akka.actor.ActorRef
import akka.http.scaladsl.model.{ HttpEntity, HttpResponse, StatusCodes }
import akka.http.scaladsl.server.{ Directives, Route }
import akka.util.Timeout
import com.todolist.models.{ AddTaskTodoListRequestHttp, CompleteTaskRequestHttp, CreateTodoListRequestHttp }
import com.todolist.services.TodoListService
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._

import scala.concurrent.ExecutionContext
import scala.util.{ Failure, Success }

class TodoListRoutes(todoListActor: ActorRef)(implicit timeout: Timeout, ec: ExecutionContext) extends Directives {

  val todoListService = new TodoListService(todoListActor)

  def routes: Route = {
    pathEndOrSingleSlash {
      complete("Server up and running")
    } ~ path("health") {
      get {
        complete(StatusCodes.OK, "Everything is great!")
      }
    } ~ pathPrefix("todolist") {
      pathEnd {
        get {
          val future = todoListService.GetTodoList()
          onComplete(future) {
            case Success(todoList) => todoList match {
              case _ => {
                complete(todoList)
              }
            }
            case Failure(e) => {
              complete(HttpResponse(StatusCodes.BadRequest, entity = HttpEntity(e.getMessage)))
            }
          }
        } ~ post {
          entity(as[CreateTodoListRequestHttp]) { request =>
            val future = todoListService.CreateTodoList(request.name)
            onComplete(future) {
              case Success(todoList) => todoList match {
                case _ => {
                  complete(todoList)
                }
              }
              case Failure(e) => {
                complete(HttpResponse(StatusCodes.BadRequest, entity = HttpEntity(e.getMessage)))
              }
            }
          }
        }
      } ~ pathPrefix(Segment) { todoListId =>
        get {
          val future = todoListService.GetToDoListsById(todoListId)
          onComplete(future) {
            case Success(todoList) => todoList match {
              case _ => {
                complete(todoList)
              }
            }
            case Failure(e) => {
              complete(HttpResponse(StatusCodes.BadRequest, entity = HttpEntity(e.getMessage)))
            }
          }
        } ~ pathPrefix("task") {
          post {
            entity(as[AddTaskTodoListRequestHttp]) { request =>
              val future = todoListService.AddTaskTodoList(todoListId, request.title)
              onComplete(future) {
                case Success(task) => task match {
                  case _ => {
                    complete(task)
                  }
                }
                case Failure(e) => {
                  complete(HttpResponse(StatusCodes.BadRequest, entity = HttpEntity(e.getMessage)))
                }
              }
            }
          } ~ pathPrefix(Segment) { taskId =>
            put {
              entity(as[CompleteTaskRequestHttp]) { request =>
                val future = todoListService.CompleteTaskById(todoListId, taskId, request.done)
                onComplete(future) {
                  case Success(_) => {
                    complete(StatusCodes.OK)
                  }
                  case Failure(e) => {
                    complete(HttpResponse(StatusCodes.BadRequest, entity = HttpEntity(e.getMessage)))
                  }
                }
              }
            } ~ delete {
              val future = todoListService.DeleteTaskById(todoListId, taskId)
              onComplete(future) {
                case Success(_) => {
                  complete(StatusCodes.OK)
                }
                case Failure(e) => {
                  complete(HttpResponse(StatusCodes.BadRequest, entity = HttpEntity(e.getMessage)))
                }
              }
            }
          }
        } ~ delete {
          val future = todoListService.DeleteToDoListsById(todoListId)
          onComplete(future) {
            case Success(_) => {
              complete(StatusCodes.OK)
            }
            case Failure(e) => {
              complete(HttpResponse(StatusCodes.BadRequest, entity = HttpEntity(e.getMessage)))
            }
          }
        }
      }
    }
  }

}