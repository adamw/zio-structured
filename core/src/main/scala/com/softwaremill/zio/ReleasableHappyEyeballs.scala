package com.softwaremill.zio

import zio.Exit.{Failure, Success}
import zio._
import zio.clock.Clock
import zio.duration._

object ReleasableHappyEyeballs {
  def apply[R, T](
      tasks: List[ZIO[R, Throwable, T]],
      delay: Duration,
      releaseExtra: T => ZIO[R, Nothing, Unit]
  ): ZIO[R with Clock, Throwable, T] =
    for {
      successful <- Queue.bounded[T](tasks.size)
      enqueueingTasks = tasks.map {
        _.onExit {
          case Success(value) => successful.offer(value)
          case Failure(_)     => ZIO.unit
        }
      }
      _ <- HappyEyeballs(enqueueingTasks, delay)
      // there has to be at least one, otherwise HE would fail
      first :: other <- successful.takeAll
      _ <- ZIO.foreach(other)(releaseExtra)
    } yield first
}
