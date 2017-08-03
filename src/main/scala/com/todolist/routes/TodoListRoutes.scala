package com.todolist.routes

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{ Directives, Route }
import akka.util.Timeout

class TodoListRoutes(implicit timeout: Timeout) extends Directives {

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
          complete("Ok")
        } ~ post {
          complete("Creating todolist")
        }
      } ~ pathPrefix(Segment) { todoListId =>
        get {
          complete(todoListId)
        } ~ pathPrefix("task") {
          post {
            complete("Create task")
          } ~ pathPrefix(Segment) { taskId =>
            put {
              complete("Update task")
            } ~ delete {
              complete("Delete task")
            }
          }
        } ~ delete {
          complete("Delete todolistid")
        }
      }
    }
  }

}