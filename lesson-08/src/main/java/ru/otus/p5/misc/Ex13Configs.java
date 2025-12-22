package ru.otus.p5.misc;

import org.apache.kafka.clients.admin.AlterConfigOp;
import org.apache.kafka.clients.admin.Config;
import org.apache.kafka.clients.admin.ConfigEntry;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.config.ConfigResource;
import org.apache.kafka.common.config.TopicConfig;
import ru.otus.RemoveAll;
import ru.otus.Utils;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Ex13Configs {

    public static void main(String[] args) throws Exception {
        Utils.doAdminAction(client -> {
            RemoveAll.removeAll(client);

            List<NewTopic> topics = List.of(
                    new NewTopic("t1", 1, (short) 1)
                            .configs(Map.of(
                                    TopicConfig.SEGMENT_MS_CONFIG, Integer.valueOf(1000 * 60 * 60).toString()
                            )),
                    new NewTopic("t2", 1, (short) 2)
            );
            Set<String> topicNames = topics.stream()
                    .map(NewTopic::name)
                    .collect(Collectors.toSet());

            RemoveAll.checkRemoval(client, topicNames);

            client.createTopics(topics)
                    .all().get();


            var broker3 = new ConfigResource(ConfigResource.Type.BROKER, "3");
            var topic1 = new ConfigResource(ConfigResource.Type.TOPIC, "t1");
            var topic2 = new ConfigResource(ConfigResource.Type.TOPIC, "t2");
            var results = client.describeConfigs(List.of(broker3, topic1, topic2)).all().get();
            Utils.log.info("Broker-5:\n{}", printConfig(results.get(broker3)));
            Utils.log.info("Topic-1:\n{}", printConfig(results.get(topic1)));
            Utils.log.info("Topic-2:\n{}", printConfig(results.get(topic2)));

            var segmentMs = new ConfigEntry("segment.ms", "60000");
            client.incrementalAlterConfigs(Map.of(topic1, List.of(new AlterConfigOp(segmentMs, AlterConfigOp.OpType.SET))))
                    .all()
                    .get();

            results = client.describeConfigs(List.of(topic1))
                    .all()
                    .get();
            Utils.log.info("Topic-1: {}", results.get(topic1));
        });
    }

    private static String printConfig(Config config) {
        var b = new StringBuilder();
        for (var v: config.entries()) {
            b.append(v).append("\n");
        }
        return b.toString();
    }

}
