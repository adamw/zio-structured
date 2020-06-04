package com.softwaremill.zio

import zio._
import zio.duration._
import zio.blocking._
import zio.console._
import java.net.{InetAddress, Socket}

object TestSocket extends App {
  override def run(args: List[String]): ZIO[zio.ZEnv, Nothing, ExitCode] = {
    effectBlocking(InetAddress.getAllByName("debian.org").toList)
      .map { addresses =>
        addresses.map(a => effectBlocking(new Socket(a, 443)))
      }
      .flatMap(tasks => ReleasableHappyEyeballs(tasks, 250.milliseconds, closeSocket))
      .tap(v => putStrLn(s"Connected: ${v.getInetAddress}"))
      .tapError(error => putStrLn(s"ERROR: $error"))
      .fold(_ => ExitCode.success, _ => ExitCode.failure)
  }

  def closeSocket(s: Socket): ZIO[Blocking, Nothing, Unit] = effectBlocking(s.close()).catchAll(_ => ZIO.unit)
}
