package com.todolist.models

sealed trait RequestHttp

case class CompleteTaskRequestHttp(done: Boolean) extends RequestHttp
case class CreateTodoListRequestHttp(name: String) extends RequestHttp
case class AddTaskTodoListRequestHttp(title: String) extends RequestHttp
