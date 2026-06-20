package otus.akka.streams.example

import akka.actor.ActorSystem
import akka.stream.IOResult
import akka.stream.scaladsl.FileIO

import java.nio.file.Paths
import scala.concurrent.{ExecutionContextExecutor, Future}

object FileExample extends App {
  implicit val system: ActorSystem = ActorSystem()
  implicit val ec: ExecutionContextExecutor = system.dispatcher

  private val resultF: Future[IOResult] = FileIO.fromPath(Paths.get("src/main/resources/input.txt"))
    .to(FileIO.toPath(Paths.get("src/main/resources/output.txt")))
    .run()

  resultF.onComplete(ioResult => {
    println(ioResult)
    system.terminate()
  })
}
