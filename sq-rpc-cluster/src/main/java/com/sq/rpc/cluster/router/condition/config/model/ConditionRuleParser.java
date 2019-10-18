package com.sq.rpc.cluster.router.condition.config.model;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import com.sq.common.utils.CollectionUtils;

/**
 * %YAML1.2
 *
 * scope: application
 * runtime: true
 * force: false
 * conditions:
 *   - >
 *     method!=sayHello =>
 *   - >
 *     ip=127.0.0.1
 *     =>
 *     1.1.1.1
 */
public class ConditionRuleParser {

    public static ConditionRouterRule parse(String rawRule) {
        Constructor constructor = new Constructor(ConditionRouterRule.class);

        Yaml yaml = new Yaml(constructor);
        ConditionRouterRule rule = yaml.load(rawRule);
        rule.setRawRule(rawRule);
        if (CollectionUtils.isEmpty(rule.getConditions())) {
            rule.setValid(false);
        }

        return rule;
    }

}
