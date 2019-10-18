package com.sq.rpc.cluster.router.tag.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.sq.rpc.cluster.router.AbstractRouterRule;

/**
 * %YAML1.2
 * ---
 * force: true
 * runtime: false
 * enabled: true
 * priority: 1
 * key: demo-provider
 * tags:
 * - name: tag1
 * addresses: [ip1, ip2]
 * - name: tag2
 * addresses: [ip3, ip4]
 * ...
 */
public class TagRouterRule extends AbstractRouterRule {
    private List<Tag> tags;

    private Map<String, List<String>> addressToTagnames = new HashMap<>();
    private Map<String, List<String>> tagnameToAddresses = new HashMap<>();

    public void init() {
        if (!isValid()) {
            return;
        }

        tags.forEach(tag -> {
            tagnameToAddresses.put(tag.getName(), tag.getAddresses());
            tag.getAddresses().forEach(addr -> {
                List<String> tagNames = addressToTagnames.computeIfAbsent(addr, k -> new ArrayList<>());
                tagNames.add(tag.getName());
            });
        });
    }

    public List<String> getAddresses() {
        return tags.stream().flatMap(tag -> tag.getAddresses().stream()).collect(Collectors.toList());
    }

    public List<String> getTagNames() {
        return tags.stream().map(Tag::getName).collect(Collectors.toList());
    }

    public Map<String, List<String>> getAddressToTagnames() {
        return addressToTagnames;
    }


    public Map<String, List<String>> getTagnameToAddresses() {
        return tagnameToAddresses;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }
}
