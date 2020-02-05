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
        addresses.map(a => newSocket(a, 443))
      }
      .flatMap(tasks => HappyEyeballs(tasks, 250.milliseconds))
      .tap(v => putStrLn(s"Connected: ${v.getInetAddress}"))
      .tapError(error => putStrLn(s"ERROR: $error"))
      .fold(_ => 1, _ => 0)
  }

  def newSocket(address: InetAddress, port: Int): ZIO[Blocking, Throwable, Socket] = {
    // the interrupt might have happened when the socket is already established.
    // In this case, we need to cleanup and close the socket.
    effectBlocking(new Socket(address, port)).onExit {
      case Exit.Success(socket) => effectBlocking(socket.close()).catchAll(_ => ZIO.unit)
      case Exit.Failure(_) => ZIO.unit
    }
  }
}
