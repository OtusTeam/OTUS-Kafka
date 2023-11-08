(defproject ru.vzaigrin.examples.kafka.base.clj.producer "1.0"
  :description "Base Kafka Producer on Clojure"
  :url "https://gitflic.ru/project/vzaigrin/examples/Kafka/Base/Clojure/producer"
  :license {:name "Apache License, Version 2.0"
            :url "http://www.apache.org/licenses/LICENSE-2.0"}
  :dependencies [[org.clojure/clojure "1.11.1"],
                 [org.apache.kafka/kafka-clients "3.4.0"]
                 [ch.qos.logback/logback-classic "1.4.7"]
                 [org.slf4j/slf4j-api "2.0.7"]]
  :main ^:skip-aot ru.vzaigrin.examples.kafka.base.clj.producer
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
