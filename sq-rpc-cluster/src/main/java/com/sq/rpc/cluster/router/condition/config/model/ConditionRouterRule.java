package com.sq.rpc.cluster.router.condition.config.model;

import java.util.List;

import com.sq.rpc.cluster.router.AbstractRouterRule;


/**
 *
 */
public class ConditionRouterRule extends AbstractRouterRule {
    public ConditionRouterRule() {
    }

    private List<String> conditions;

    public List<String> getConditions() {
        return conditions;
    }

    public void setConditions(List<String> conditions) {
        this.conditions = conditions;
    }
}
