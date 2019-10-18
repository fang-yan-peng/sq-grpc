package com.sq.registry.integration;


import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sq.common.utils.StringUtils;
import com.sq.rpc.cluster.Configurator;
import com.sq.rpc.cluster.configurator.parser.ConfigParser;
import com.sq.rpc.cluster.router.condition.config.center.ConfigChangeEvent;
import com.sq.rpc.cluster.router.condition.config.center.ConfigChangeType;
import com.sq.rpc.cluster.router.condition.config.center.ConfigurationListener;
import com.sq.rpc.cluster.router.condition.config.center.DynamicConfiguration;

/**
 * AbstractConfiguratorListener
 */
public abstract class AbstractConfiguratorListener implements ConfigurationListener {
    private static final Logger logger = LoggerFactory.getLogger(AbstractConfiguratorListener.class);

    protected List<Configurator> configurators = Collections.emptyList();


    protected final void initWith(String key) {
        DynamicConfiguration dynamicConfiguration = DynamicConfiguration.getDynamicConfiguration();
        dynamicConfiguration.addListener(key, this);
        String rawConfig = dynamicConfiguration.getConfig(key);
        if (!StringUtils.isEmpty(rawConfig)) {
            process(new ConfigChangeEvent(key, rawConfig));
        }
    }

    @Override
    public void process(ConfigChangeEvent event) {
        if (logger.isInfoEnabled()) {
            logger.info("Notification of overriding rule, change type is: " + event.getChangeType() +
                    ", raw config content is:\n " + event.getValue());
        }

        if (event.getChangeType().equals(ConfigChangeType.DELETED)) {
            configurators.clear();
        } else {
            try {
                // parseConfigurators will recognize app/services config automatically.
                configurators = Configurator.toConfigurators(ConfigParser.parseConfigurators(event.getValue()))
                        .orElse(configurators);
            } catch (Exception e) {
                logger.error("Failed to parse raw dynamic config and it will not take effect, the raw config is: " +
                        event.getValue(), e);
                return;
            }
        }

        notifyOverrides();
    }

    protected abstract void notifyOverrides();

    public List<Configurator> getConfigurators() {
        return configurators;
    }

    public void setConfigurators(List<Configurator> configurators) {
        this.configurators = configurators;
    }
}
