package com.todolist.test

import com.typesafe.config.ConfigFactory

object PersistenceSuite {

  def journalId() = "dummy-journal"

  def snapStoreId() = "dummy-snapshot-store"

  def config() = ConfigFactory.parseString(
    """akka.loggers = [akka.testkit.TestEventListener] # makes both log-snooping and logging work
         akka.loglevel = "DEBUG"
         akka.persistence.journal.plugin = "dummy-journal"
         akka.persistence.snapshot-store.plugin = "dummy-snapshot-store"
         dummy-journal {
           class = "org.dmonix.akka.persistence.JournalPlugin"
           plugin-dispatcher = "akka.actor.default-dispatcher"
         }
        dummy-snapshot-store {
          class = "org.dmonix.akka.persistence.SnapshotStorePlugin"
          plugin-dispatcher = "akka.persistence.dispatchers.default-plugin-dispatcher"
         }
         akka.actor.debug.receive = on"""
  )
}
