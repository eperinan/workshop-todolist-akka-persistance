package com.todolist.model

sealed trait Requests

case class GetToDoListsRequests() extends Requests
case class GetToDoListsByIdRequests(todoListId: String) extends Requests
case class DeleteToDoListRequests(todoListId: String) extends Requests
