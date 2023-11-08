(ns ru.vzaigrin.examples.kafka.base.clj.producer
  (:gen-class)
  (:import 
   (java.util Properties)
   (org.apache.kafka.clients.producer KafkaProducer ProducerConfig ProducerRecord)))

(defn -main
  "Base Kafka Producer"
  []
  (let [topic "test"
        props (doto (Properties.)
                (.put ProducerConfig/BOOTSTRAP_SERVERS_CONFIG "localhost:9092")
                (.put ProducerConfig/KEY_SERIALIZER_CLASS_CONFIG "org.apache.kafka.common.serialization.InetgerSerializer")
                (.put ProducerConfig/VALUE_SERIALIZER_CLASS_CONFIG "org.apache.kafka.common.serialization.StringSerializer"))]
    (with-open [producer (KafkaProducer. props)]
      (doseq [i (range 1 1001)] (.send producer (ProducerRecord.  topic i (str "Message " i))))
      (.flush producer))))
