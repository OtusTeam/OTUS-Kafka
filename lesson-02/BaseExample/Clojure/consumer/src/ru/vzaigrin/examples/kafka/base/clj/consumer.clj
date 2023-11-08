(ns ru.vzaigrin.examples.kafka.base.clj.consumer
  (:gen-class)
  (:import
   (java.time Duration)
   (java.util Properties)
   (org.apache.kafka.clients.consumer ConsumerConfig KafkaConsumer)))

(defn -main
  "Base Kafka Consumer"
  []
  (let [topic "test"
        props (doto (Properties.)
                (.put ConsumerConfig/BOOTSTRAP_SERVERS_CONFIG "localhost:29092")
                (.put ConsumerConfig/KEY_DESERIALIZER_CLASS_CONFIG "org.apache.kafka.common.serialization.LongDeserializer")
                (.put ConsumerConfig/VALUE_DESERIALIZER_CLASS_CONFIG "org.apache.kafka.common.serialization.StringDeserializer")
                (.put ConsumerConfig/GROUP_ID_CONFIG "g1")
                (.put ConsumerConfig/AUTO_OFFSET_RESET_CONFIG "earliest")
                (.put ConsumerConfig/ENABLE_AUTO_COMMIT_CONFIG "false"))]
    (with-open [consumer (KafkaConsumer. props)]
      (.subscribe consumer [topic])
      (while true 
        (doseq [msg (.poll consumer (Duration/ofSeconds 1))]
          (printf "%d\t%d\t%d\t%s\n"
                  (.partition msg)
                  (.offset msg)
                  (.key msg)
                  (.value msg)))))))
