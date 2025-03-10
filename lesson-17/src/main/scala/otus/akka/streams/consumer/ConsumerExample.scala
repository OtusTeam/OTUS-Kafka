package otus.akka.streams.consumer

import akka.Done
import akka.actor.ActorSystem
import akka.kafka.scaladsl.{Committer, Consumer}
import akka.kafka.{CommitterSettings, ConsumerSettings, Subscriptions}
import akka.stream.RestartSettings
import akka.stream.scaladsl.{RestartSource, Sink, Source}
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer

import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration.DurationInt
import scala.util.Random


object ConsumerExample extends App {
  implicit val system: ActorSystem = ActorSystem()
  implicit val ec: ExecutionContextExecutor = system.dispatcher

  private val config = system.settings.config.getConfig("kafka.consumer")
  private val consumerSettings: ConsumerSettings[String, String] = ConsumerSettings(config, new StringDeserializer, new StringDeserializer)
    .withGroupId("lesson-17-test")
    .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
  private val topic = system.settings.config.getConfig("kafka").getString("topic")
  private val committerSettings: CommitterSettings = CommitterSettings(system.settings.config.getConfig("akka.kafka.committer"))
  private val consumerSource: Source[Done, Consumer.Control] = Consumer
    .committableSource(consumerSettings, Subscriptions.topics(topic))
    .map { msg =>
      process(msg.record.value)
      msg.committableOffset
    }
    .via(Committer.flow(committerSettings.withMaxBatch(1)))
  private val restartSettings = RestartSettings(minBackoff = 3.seconds, maxBackoff = 30.seconds, randomFactor = 0.2)


  RestartSource
    .onFailuresWithBackoff(restartSettings) { () =>
      consumerSource
    }
    .runWith(Sink.seq)

  private def process(value: String): Unit = {
    val random = Random.nextInt(100)
    println(s"random: $random")
    if (random % 20 == 0) {
      throw new RuntimeException()
    } else {
      println(s"value: $value")
    }
  }
}
