package otus.akka.streams.example

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.ClosedShape
import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, RunnableGraph, Sink, Source, Zip}

import scala.concurrent.ExecutionContextExecutor

object GraphDSLExampleTest extends App {
  implicit val system: ActorSystem = ActorSystem()
  implicit val ec: ExecutionContextExecutor = system.dispatcher

  private val graph = GraphDSL.create() { implicit builder =>
    import GraphDSL.Implicits._

    val source: Source[Int, NotUsed] = Source(1 to 10)

    val broadcast = builder.add(Broadcast[Int](2))
    val zip = builder.add(Zip[Int, Int])
    source ~> broadcast.in

    val incrementFlow: Flow[Int, Int, NotUsed] = Flow.fromFunction(_ + 1)
    val squareFlow: Flow[Int, Int, NotUsed] = Flow.fromFunction(v => v * v)

    broadcast.out(0) ~> incrementFlow ~> zip.in0
    broadcast.out(1) ~> squareFlow ~> zip.in1

    zip.out ~> Sink.foreach(println)
    ClosedShape
  }

  RunnableGraph.fromGraph(graph)
  system.terminate()
}
