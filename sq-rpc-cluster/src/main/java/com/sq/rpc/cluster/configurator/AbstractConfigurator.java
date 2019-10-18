package com.sq.rpc.cluster.configurator;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.sq.common.Constants;
import com.sq.common.URL;
import com.sq.common.utils.NetUtils;
import com.sq.common.utils.StringUtils;
import com.sq.rpc.cluster.Configurator;

/**
 * AbstractOverrideConfigurator
 */
public abstract class AbstractConfigurator implements Configurator {

    private final URL configuratorUrl;

    public AbstractConfigurator(URL url) {
        if (url == null) {
            throw new IllegalArgumentException("configurator url == null");
        }
        this.configuratorUrl = url;
    }

    @Override
    public URL getUrl() {
        return configuratorUrl;
    }

    @Override
    public URL configure(URL url) {
        // If override url is not enabled or is invalid, just return.
        if (!configuratorUrl.getParameter(Constants.ENABLED_KEY, true) || configuratorUrl.getHost() == null || url == null || url.getHost() == null) {
            return url;
        }
        /**
         * This if branch is created since 2.7.0.
         */
        String apiVersion = configuratorUrl.getParameter(Constants.CONFIG_VERSION_KEY);
        if (StringUtils.isNotEmpty(apiVersion)) {
            String currentSide = url.getParameter(Constants.SIDE_KEY);
            String configuratorSide = configuratorUrl.getParameter(Constants.SIDE_KEY);
            if (currentSide.equals(configuratorSide) && Constants.CONSUMER.equals(configuratorSide) && 0 == configuratorUrl.getPort()) {
                url = configureIfMatch(NetUtils.getLocalHost(), url);
            } else if (currentSide.equals(configuratorSide) && Constants.PROVIDER.equals(configuratorSide) && url.getPort() == configuratorUrl.getPort()) {
                url = configureIfMatch(url.getHost(), url);
            }
        }
        return url;
    }

    private URL configureIfMatch(String host, URL url) {
        if (Constants.ANYHOST_VALUE.equals(configuratorUrl.getHost()) || host.equals(configuratorUrl.getHost())) {
            // TODO, to support wildcards
            String providers = configuratorUrl.getParameter(Constants.OVERRIDE_PROVIDERS_KEY);
            if (StringUtils.isEmpty(providers) || providers.contains(url.getAddress()) || providers.contains(Constants.ANYHOST_VALUE)) {
                String configApplication = configuratorUrl.getParameter(Constants.APPLICATION_KEY,
                        configuratorUrl.getUsername());
                String currentApplication = url.getParameter(Constants.APPLICATION_KEY, url.getUsername());
                if (configApplication == null || Constants.ANY_VALUE.equals(configApplication)
                        || configApplication.equals(currentApplication)) {
                    Set<String> conditionKeys = new HashSet<String>();
                    conditionKeys.add(Constants.CATEGORY_KEY);
                    conditionKeys.add(Constants.CHECK_KEY);
                    conditionKeys.add(Constants.DYNAMIC_KEY);
                    conditionKeys.add(Constants.ENABLED_KEY);
                    conditionKeys.add(Constants.GROUP_KEY);
                    conditionKeys.add(Constants.VERSION_KEY);
                    conditionKeys.add(Constants.APPLICATION_KEY);
                    conditionKeys.add(Constants.SIDE_KEY);
                    conditionKeys.add(Constants.CONFIG_VERSION_KEY);
                    for (Map.Entry<String, String> entry : configuratorUrl.getParameters().entrySet()) {
                        String key = entry.getKey();
                        String value = entry.getValue();
                        if (key.startsWith("~") || Constants.APPLICATION_KEY.equals(key) || Constants.SIDE_KEY.equals(key)) {
                            conditionKeys.add(key);
                            if (value != null && !Constants.ANY_VALUE.equals(value)
                                    && !value.equals(url.getParameter(key.startsWith("~") ? key.substring(1) : key))) {
                                return url;
                            }
                        }
                    }
                    return doConfigure(url, configuratorUrl.removeParameters(conditionKeys));
                }
            }
        }
        return url;
    }

    protected abstract URL doConfigure(URL currentUrl, URL configUrl);

}
