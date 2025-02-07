package ru.otus.p5.misc;

import ru.otus.Utils;

public class Ex14DescribeCluster {

    public static void main(String[] args) throws Exception {
        Utils.doAdminAction(client -> {
            var res = client.describeCluster();
            Utils.log.info("Controller: {}", res.controller().get());
            Utils.log.info("Nodes:\n{}", res.nodes().get());
        });
    }

}
