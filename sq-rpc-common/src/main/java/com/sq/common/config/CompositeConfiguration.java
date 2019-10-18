package com.sq.common.config;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 */
public class CompositeConfiguration implements Configuration {
    private Logger logger = LoggerFactory.getLogger(CompositeConfiguration.class);

    /**
     * List holding all the configuration
     */
    private List<Configuration> configList = new LinkedList<Configuration>();

    public CompositeConfiguration() {

    }

    public CompositeConfiguration(Configuration... configurations) {
        if (configurations != null && configurations.length > 0) {
            Arrays.stream(configurations).filter(config -> !configList.contains(config)).forEach(configList::add);
        }
    }

    public void addConfiguration(Configuration configuration) {
        if (configList.contains(configuration)) {
            return;
        }
        this.configList.add(configuration);
    }

    public void addConfigurationFirst(Configuration configuration) {
        this.addConfiguration(0, configuration);
    }

    public void addConfiguration(int pos, Configuration configuration) {
        this.configList.add(pos, configuration);
    }

    @Override
    public Object getInternalProperty(String key) {
        Configuration firstMatchingConfiguration = null;
        for (Configuration config : configList) {
            try {
                if (config.containsKey(key)) {
                    firstMatchingConfiguration = config;
                    break;
                }
            } catch (Exception e) {
                logger.error("Error when trying to get value for key " + key + " from " + config + ", will continue to try the next one.");
            }
        }
        if (firstMatchingConfiguration != null) {
            return firstMatchingConfiguration.getProperty(key);
        } else {
            return null;
        }
    }

    @Override
    public boolean containsKey(String key) {
        return configList.stream().anyMatch(c -> c.containsKey(key));
    }
}
