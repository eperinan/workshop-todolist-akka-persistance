package com.todolist.routes

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.PathDirectives.pathEndOrSingleSlash
import akka.http.scaladsl.server.directives.RouteDirectives.complete

object BaseRoutes {
  lazy val routes: Route =
    pathEndOrSingleSlash {
      complete("Server up and running")
    }
}
