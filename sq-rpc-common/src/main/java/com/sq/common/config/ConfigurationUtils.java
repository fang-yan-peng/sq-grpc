package com.sq.common.config;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sq.common.Constants;
import com.sq.common.utils.StringUtils;

/**
 * Utilities for manipulating configurations from different sources
 */
public class ConfigurationUtils {
    private static final Logger logger = LoggerFactory.getLogger(ConfigurationUtils.class);

    // FIXME
    @SuppressWarnings("deprecation")
    public static int getServerShutdownTimeout() {
        int timeout = Constants.DEFAULT_SERVER_SHUTDOWN_TIMEOUT;
        Configuration configuration = Environment.getInstance().getConfiguration();
        String value = StringUtils.trim(configuration.getString(Constants.SHUTDOWN_WAIT_KEY));

        if (value != null && value.length() > 0) {
            try {
                timeout = Integer.parseInt(value);
            } catch (Exception e) {
                // ignore
            }
        } else {
            value = StringUtils.trim(configuration.getString(Constants.SHUTDOWN_WAIT_SECONDS_KEY));
            if (value != null && value.length() > 0) {
                try {
                    timeout = Integer.parseInt(value) * 1000;
                } catch (Exception e) {
                    // ignore
                }
            }
        }
        return timeout;
    }

    public static String getProperty(String property) {
        return getProperty(property, null);
    }

    public static String getProperty(String property, String defaultValue) {
        return StringUtils.trim(Environment.getInstance().getConfiguration().getString(property, defaultValue));
    }

    public static Map<String, String> parseProperties(String content) throws IOException {
        Map<String, String> map = new HashMap<>();
        if (StringUtils.isEmpty(content)) {
            logger.warn("You specified the config centre, but there's not even one single config item in it.");
        } else {
            Properties properties = new Properties();
            properties.load(new StringReader(content));
            properties.stringPropertyNames().forEach(
                    k -> map.put(k, properties.getProperty(k))
            );
        }
        return map;
    }

}
