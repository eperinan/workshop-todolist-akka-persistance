package com.todolist.actors

import java.util.concurrent.TimeUnit

import akka.actor.{ Actor, ActorLogging }
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import akka.util.Timeout
import com.todolist.models.StartServer
import com.todolist.routes.TodoListRoutes
import com.typesafe.config.ConfigFactory

class ServerHttpActor extends Actor with ActorLogging {

  implicit val system = context.system
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher
  implicit val timeout = Timeout(5, TimeUnit.SECONDS)

  val config = ConfigFactory.load()

  override def preStart() = {
    log.info(s"${self.path} Starting ServerHttpActor ...")
  }

  override def preRestart(reason: Throwable, message: Option[Any]) {
    log.error(reason, s"${self.path} Restarting due to [{}] when processing [{}]", reason.getMessage, message.getOrElse(""))
  }

  def receive = {
    case StartServer(todoListActor) => {
      val todoListRoutes = new TodoListRoutes(todoListActor)
      Http().bindAndHandle(todoListRoutes.routes, config.getString("todolist-akka-training.hostname"), config.getInt("todolist-akka-training.port"))
      log.info(s"${self.path} Running ServerHttpActor")
    }
  }

}
