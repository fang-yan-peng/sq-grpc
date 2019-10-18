package com.sq.rpc.cluster.router.condition.config;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sq.common.URL;
import com.sq.common.exceptions.RpcException;
import com.sq.common.utils.CollectionUtils;
import com.sq.common.utils.StringUtils;
import com.sq.rpc.Invocation;
import com.sq.rpc.Invoker;
import com.sq.rpc.cluster.Router;
import com.sq.rpc.cluster.router.AbstractRouter;
import com.sq.rpc.cluster.router.condition.ConditionRouter;
import com.sq.rpc.cluster.router.condition.config.center.ConfigChangeEvent;
import com.sq.rpc.cluster.router.condition.config.center.ConfigChangeType;
import com.sq.rpc.cluster.router.condition.config.center.ConfigurationListener;
import com.sq.rpc.cluster.router.condition.config.center.DynamicConfiguration;
import com.sq.rpc.cluster.router.condition.config.model.ConditionRouterRule;
import com.sq.rpc.cluster.router.condition.config.model.ConditionRuleParser;

/**
 * Abstract router which listens to dynamic configuration
 */
public abstract class ListenableRouter extends AbstractRouter implements ConfigurationListener {
    public static final String NAME = "LISTENABLE_ROUTER";
    private static final String RULE_SUFFIX = ".condition-router";

    private static final Logger logger = LoggerFactory.getLogger(ListenableRouter.class);
    private ConditionRouterRule routerRule;
    private List<ConditionRouter> conditionRouters = Collections.emptyList();

    public ListenableRouter(DynamicConfiguration configuration, URL url, String ruleKey) {
        super(configuration, url);
        this.force = false;
        this.init(ruleKey);
    }

    @Override
    public synchronized void process(ConfigChangeEvent event) {
        if (logger.isInfoEnabled()) {
            logger.info("Notification of condition rule, change type is: " + event.getChangeType() +
                    ", raw rule is:\n " + event.getValue());
        }

        if (event.getChangeType().equals(ConfigChangeType.DELETED)) {
            routerRule = null;
            conditionRouters = Collections.emptyList();
        } else {
            try {
                routerRule = ConditionRuleParser.parse(event.getValue());
                generateConditions(routerRule);
            } catch (Exception e) {
                logger.error("Failed to parse the raw condition rule and it will not take effect, please check " +
                        "if the condition rule matches with the template, the raw rule is:\n " + event.getValue(), e);
            }
        }
    }

    @Override
    public <T> List<Invoker<T>> route(List<Invoker<T>> invokers, URL url, Invocation invocation) throws RpcException {
        if (CollectionUtils.isEmpty(invokers) || conditionRouters.size() == 0) {
            return invokers;
        }

        // We will check enabled status inside each router.
        for (Router router : conditionRouters) {
            invokers = router.route(invokers, url, invocation);
        }

        return invokers;
    }

    @Override
    public int getPriority() {
        return DEFAULT_PRIORITY;
    }

    @Override
    public boolean isForce() {
        return (routerRule != null && routerRule.isForce());
    }

    private boolean isRuleRuntime() {
        return routerRule != null && routerRule.isValid() && routerRule.isRuntime();
    }

    private void generateConditions(ConditionRouterRule rule) {
        if (rule != null && rule.isValid()) {
            this.conditionRouters = rule.getConditions()
                    .stream()
                    .map(condition -> new ConditionRouter(condition, rule.isForce(), rule.isEnabled()))
                    .collect(Collectors.toList());
        }
    }

    private synchronized void init(String ruleKey) {
        if (StringUtils.isEmpty(ruleKey)) {
            return;
        }
        String routerKey = ruleKey + RULE_SUFFIX;
        configuration.addListener(routerKey, this);
        String rule = configuration.getConfig(routerKey);
        if (StringUtils.isNotEmpty(rule)) {
            this.process(new ConfigChangeEvent(routerKey, rule));
        }
    }
}
