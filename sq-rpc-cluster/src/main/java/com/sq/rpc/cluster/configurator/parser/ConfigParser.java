package com.sq.rpc.cluster.configurator.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import com.sq.common.Constants;
import com.sq.common.URL;
import com.sq.common.utils.CollectionUtils;
import com.sq.common.utils.StringUtils;
import com.sq.rpc.cluster.configurator.parser.model.ConfigItem;
import com.sq.rpc.cluster.configurator.parser.model.ConfiguratorConfig;

/**
 * Config parser
 */
public class ConfigParser {

    public static List<URL> parseConfigurators(String rawConfig) {
        List<URL> urls = new ArrayList<>();
        ConfiguratorConfig configuratorConfig = parseObject(rawConfig);

        String scope = configuratorConfig.getScope();
        List<ConfigItem> items = configuratorConfig.getConfigs();

        if (ConfiguratorConfig.SCOPE_APPLICATION.equals(scope)) {
            items.forEach(item -> urls.addAll(appItemToUrls(item, configuratorConfig)));
        } else {
            // services scope by default.
            items.forEach(item -> urls.addAll(serviceItemToUrls(item, configuratorConfig)));
        }
        return urls;
    }

    private static <T> T parseObject(String rawConfig) {
        Constructor constructor = new Constructor(ConfiguratorConfig.class);
        TypeDescription itemDescription = new TypeDescription(ConfiguratorConfig.class);
        itemDescription.addPropertyParameters("items", ConfigItem.class);
        constructor.addTypeDescription(itemDescription);

        Yaml yaml = new Yaml(constructor);
        return yaml.load(rawConfig);
    }

    private static List<URL> serviceItemToUrls(ConfigItem item, ConfiguratorConfig config) {
        List<URL> urls = new ArrayList<>();
        List<String> addresses = parseAddresses(item);

        addresses.forEach(addr -> {
            StringBuilder urlBuilder = new StringBuilder();
            urlBuilder.append("override://").append(addr).append("/");

            urlBuilder.append(appendService(config.getKey()));
            urlBuilder.append(toParameterString(item));

            parseEnabled(item, config, urlBuilder);

            urlBuilder.append("&category=").append(Constants.DYNAMIC_CONFIGURATORS_CATEGORY);
            urlBuilder.append("&configVersion=").append(config.getConfigVersion());

            List<String> apps = item.getApplications();
            if (apps != null && apps.size() > 0) {
                apps.forEach(app -> {
                    urls.add(URL.valueOf(urlBuilder.append("&application=").append(app).toString()));
                });
            } else {
                urls.add(URL.valueOf(urlBuilder.toString()));
            }
        });

        return urls;
    }

    private static List<URL> appItemToUrls(ConfigItem item, ConfiguratorConfig config) {
        List<URL> urls = new ArrayList<>();
        List<String> addresses = parseAddresses(item);
        for (String addr : addresses) {
            StringBuilder urlBuilder = new StringBuilder();
            urlBuilder.append("override://").append(addr).append("/");
            List<String> services = item.getServices();
            if (services == null) {
                services = new ArrayList<>();
            }
            if (services.size() == 0) {
                services.add("*");
            }
            for (String s : services) {
                urlBuilder.append(appendService(s));
                urlBuilder.append(toParameterString(item));

                urlBuilder.append("&application=").append(config.getKey());

                parseEnabled(item, config, urlBuilder);

                urlBuilder.append("&category=").append(Constants.APP_DYNAMIC_CONFIGURATORS_CATEGORY);
                urlBuilder.append("&configVersion=").append(config.getConfigVersion());

                urls.add(URL.valueOf(urlBuilder.toString()));
            }
        }
        return urls;
    }

    private static String toParameterString(ConfigItem item) {
        StringBuilder sb = new StringBuilder();
        sb.append("category=");
        sb.append(Constants.DYNAMIC_CONFIGURATORS_CATEGORY);
        if (item.getSide() != null) {
            sb.append("&side=");
            sb.append(item.getSide());
        }
        Map<String, String> parameters = item.getParameters();
        if (CollectionUtils.isEmptyMap(parameters)) {
            throw new IllegalStateException("Invalid configurator rule, please specify at least one parameter " +
                    "you want to change in the rule.");
        }

        parameters.forEach((k, v) -> {
            sb.append("&");
            sb.append(k);
            sb.append("=");
            sb.append(v);
        });

        if (CollectionUtils.isNotEmpty(item.getProviderAddresses())) {
            sb.append("&");
            sb.append(Constants.OVERRIDE_PROVIDERS_KEY);
            sb.append("=");
            sb.append(CollectionUtils.join(item.getProviderAddresses(), ","));
        }

        return sb.toString();
    }

    private static String appendService(String serviceKey) {
        StringBuilder sb = new StringBuilder();
        if (StringUtils.isEmpty(serviceKey)) {
            throw new IllegalStateException("services field in configuration is null.");
        }

        String interfaceName = serviceKey;
        int i = interfaceName.indexOf("/");
        if (i > 0) {
            sb.append("group=");
            sb.append(interfaceName, 0, i);
            sb.append("&");

            interfaceName = interfaceName.substring(i + 1);
        }
        int j = interfaceName.indexOf(":");
        if (j > 0) {
            sb.append("version=");
            sb.append(interfaceName.substring(j + 1));
            sb.append("&");
            interfaceName = interfaceName.substring(0, j);
        }
        sb.insert(0, interfaceName + "?");

        return sb.toString();
    }

    private static void parseEnabled(ConfigItem item, ConfiguratorConfig config, StringBuilder urlBuilder) {
        urlBuilder.append("&enabled=");
        if (item.getType() == null || ConfigItem.GENERAL_TYPE.equals(item.getType())) {
            urlBuilder.append(config.getEnabled());
        } else {
            urlBuilder.append(item.getEnabled());
        }
    }

    private static List<String> parseAddresses(ConfigItem item) {
        List<String> addresses = item.getAddresses();
        if (addresses == null) {
            addresses = new ArrayList<>();
        }
        if (addresses.size() == 0) {
            addresses.add(Constants.ANYHOST_VALUE);
        }
        return addresses;
    }
}
