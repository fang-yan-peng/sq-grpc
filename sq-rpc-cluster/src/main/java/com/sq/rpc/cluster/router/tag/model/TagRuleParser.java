package com.sq.rpc.cluster.router.tag.model;

import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import com.sq.common.utils.CollectionUtils;

/**
 *
 */
public class TagRuleParser {

    public static TagRouterRule parse(String rawRule) {
        Constructor constructor = new Constructor(TagRouterRule.class);
        TypeDescription tagDescription = new TypeDescription(TagRouterRule.class);
        tagDescription.addPropertyParameters("tags", Tag.class);
        constructor.addTypeDescription(tagDescription);

        Yaml yaml = new Yaml(constructor);
        TagRouterRule rule = yaml.load(rawRule);
        rule.setRawRule(rawRule);
        if (CollectionUtils.isEmpty(rule.getTags())) {
            rule.setValid(false);
        }

        rule.init();
        return rule;
    }
}
