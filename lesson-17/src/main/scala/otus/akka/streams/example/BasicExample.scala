package otus.akka.streams.example

import akka.actor.ActorSystem
import akka.{Done, NotUsed}
import akka.stream.scaladsl.{Flow, RunnableGraph, Sink, Source}

import scala.concurrent.{ExecutionContextExecutor, Future}

object BasicExample extends App {
  implicit val system: ActorSystem = ActorSystem()
  implicit val ec: ExecutionContextExecutor = system.dispatcher

  private val source: Source[Int, NotUsed] = Source(1 to 100)
  private val flow: Flow[Int, Int, NotUsed] = Flow.fromFunction(_ + 1)
  private val sink: Sink[Int, Future[Done]] = Sink.foreach(println)

  source.via(flow).runWith(sink)

  system.terminate()
}
