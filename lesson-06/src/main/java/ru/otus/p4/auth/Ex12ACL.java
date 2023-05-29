package ru.otus.p4.auth;

import org.apache.kafka.common.acl.AccessControlEntry;
import org.apache.kafka.common.acl.AccessControlEntryFilter;
import org.apache.kafka.common.acl.AclBinding;
import org.apache.kafka.common.acl.AclBindingFilter;
import org.apache.kafka.common.acl.AclOperation;
import org.apache.kafka.common.acl.AclPermissionType;
import org.apache.kafka.common.resource.PatternType;
import org.apache.kafka.common.resource.ResourcePattern;
import org.apache.kafka.common.resource.ResourcePatternFilter;
import org.apache.kafka.common.resource.ResourceType;
import ru.otus.Utils;

import java.util.List;

public class Ex12ACL {

    public static void main(String[] args) throws Exception {
        Utils.doAdminActionAuth(client -> {
                    client.createAcls(List.of(new AclBinding(
                            new ResourcePattern(ResourceType.TOPIC, "secure.", PatternType.PREFIXED),
                            new AccessControlEntry("User:alice", "*", AclOperation.WRITE, AclPermissionType.ALLOW)
                    ))).all().get();

                    var res = client.describeAcls(new AclBindingFilter(
                            new ResourcePatternFilter(ResourceType.TOPIC, null, PatternType.ANY),
                            new AccessControlEntryFilter(null, "*", AclOperation.ANY, AclPermissionType.ANY)
                    )).values().get();
                    Utils.log.info("ACL: {}", res);

                    client.deleteAcls(List.of(new AclBindingFilter(
                            new ResourcePatternFilter(ResourceType.TOPIC, null, PatternType.ANY),
                            new AccessControlEntryFilter(null, "*", AclOperation.ANY, AclPermissionType.ANY)
                    )));

                    res = client.describeAcls(new AclBindingFilter(
                            new ResourcePatternFilter(ResourceType.TOPIC, null, PatternType.ANY),
                            new AccessControlEntryFilter(null, "*", AclOperation.ANY, AclPermissionType.ANY)
                    )).values().get();
                    Utils.log.info("ACL after delete: {}", res);

                });
    }

}
