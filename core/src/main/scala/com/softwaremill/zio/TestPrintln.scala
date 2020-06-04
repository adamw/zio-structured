package com.softwaremill.zio

import zio._
import zio.clock.Clock
import zio.duration._

object TestPrintln extends App {
  override def run(args: List[String]): ZIO[zio.ZEnv, Nothing, ExitCode] = {
    val start = System.currentTimeMillis()
    def log(msg: String): UIO[Unit] = UIO {
      val now = System.currentTimeMillis()
      val second = (now - start) / 1000L
      println(s"after ${second}s: $msg")
    }

    def printSleepPrint(sleep: Duration, name: String): ZIO[Clock, Nothing, String] =
      log(s"START: $name") *> URIO.sleep(sleep) *> log(s"DONE: $name") *> UIO(name)

    def printSleepFail(sleep: Duration, name: String): ZIO[Clock, Throwable, String] =
      log(s"START: $name") *> URIO.sleep(sleep) *> log(s"FAIL: $name") *> IO.fail(new RuntimeException(s"FAIL: $name"))

    // 0s-6s: 1st should be interrupted
    // 2s-3s: 2nd should fail
    // 3s-6s: 3rd succeeds
    // 5s-6s: 4th should be interrupted
    // -----:  5th shouldn't start
    val result = HappyEyeballs(
      List(
        printSleepPrint(10.seconds, "task1"),
        printSleepFail(1.second, "task2"),
        printSleepPrint(3.seconds, "task3"),
        printSleepPrint(2.seconds, "task4"),
        printSleepPrint(2.seconds, "task5")
      ),
      2.seconds
    )

    result
      .tap(v => log(s"WON: $v"))
      .tapError(error => log(s"ERROR: $error"))
      .fold(_ => ExitCode.success, _ => ExitCode.failure)
      .untraced
  }
}
