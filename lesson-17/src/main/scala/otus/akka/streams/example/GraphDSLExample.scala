package otus.akka.streams.example

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.{ClosedShape, Graph}
import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, RunnableGraph, Sink, Source, Zip}

import scala.concurrent.ExecutionContextExecutor

object GraphDSLExample extends App {
  implicit val system: ActorSystem = ActorSystem()
  implicit val ec: ExecutionContextExecutor = system.dispatcher

  private val graph: Graph[ClosedShape.type, NotUsed] = GraphDSL.create() { implicit builder =>
    import GraphDSL.Implicits._

    val broadcast = builder.add(Broadcast[Int](2))
    val source: Source[Int, NotUsed] = Source(1 to 100)

    source ~> broadcast

    val flow: Flow[Int, Int, NotUsed] = Flow.fromFunction(identity[Int])
    val squareFlow: Flow[Int, Int, NotUsed] = Flow.fromFunction(Math.pow(_, 2).toInt)

    val zip = builder.add(Zip[Int, Int])

    broadcast.out(0) ~> flow ~> zip.in0
    broadcast.out(1) ~> squareFlow ~> zip.in1

    zip.out ~> Sink.foreach(println)
    ClosedShape
  }

  RunnableGraph.fromGraph(graph).run()
  system.terminate()
}
