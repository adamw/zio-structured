package com.softwaremill.zio

import zio._
import zio.duration._
import zio.blocking._
import zio.console._
import java.net.{InetAddress, Socket}

object TestSocket extends App {
  override def run(args: List[String]): ZIO[zio.ZEnv, Nothing, Int] = {
    effectBlocking(InetAddress.getAllByName("debian.org").toList)
      .map { addresses =>
        addresses.map(a => effectBlocking(new Socket(a, 443)))
      }
      .flatMap(tasks => HappyEyeballs(tasks, 250.milliseconds))
      .tap(v => putStrLn(s"Connected: ${v.getInetAddress}"))
      .tapError(error => putStrLn("ERROR: " + error))
      .fold(_ => 1, _ => 0)
  }
}
