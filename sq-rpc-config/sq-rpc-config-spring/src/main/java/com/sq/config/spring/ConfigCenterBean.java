package com.sq.config.spring;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;

import com.sq.common.config.ConfigurationUtils;
import com.sq.common.utils.StringUtils;
import com.sq.config.ApplicationConfig;
import com.sq.config.ConfigCenterConfig;
import com.sq.config.spring.extension.SpringExtensionFactory;

/**
 * Since 2.7.0+, export and refer will only be executed when Spring is fully initialized, and each Config bean will get refreshed on the start of the export and refer process.
 * <p>
 * If use ConfigCenterConfig directly, you should make sure ConfigCenterConfig.init() is called before actually export/refer any service.
 */
public class ConfigCenterBean extends ConfigCenterConfig implements InitializingBean, ApplicationContextAware, DisposableBean, EnvironmentAware {

    private transient ApplicationContext applicationContext;

    private Boolean includeSpringEnv = false;
    private ApplicationConfig application;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        SpringExtensionFactory.addApplicationContext(applicationContext);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (getApplication() == null) {
            Map<String, ApplicationConfig> applicationConfigMap = applicationContext == null ? null : BeanFactoryUtils.beansOfTypeIncludingAncestors(applicationContext, ApplicationConfig.class, false, false);
            if (applicationConfigMap != null && applicationConfigMap.size() > 0) {
                ApplicationConfig applicationConfig = null;
                for (ApplicationConfig config : applicationConfigMap.values()) {
                    if (config.isDefault() == null || config.isDefault()) {
                        if (applicationConfig != null) {
                            throw new IllegalStateException("Duplicate application configs: " + applicationConfig + " and " + config);
                        }
                        applicationConfig = config;
                    }
                }
                if (applicationConfig != null) {
                    setApplication(applicationConfig);
                }
            }
        }
    }

    @Override
    public void destroy() throws Exception {

    }

    @Override
    public void setEnvironment(Environment environment) {
        if (includeSpringEnv) {
            Map<String, String> externalProperties = getConfigurations(getConfigFile(), environment);
            Map<String, String> appExternalProperties = getConfigurations(StringUtils.isNotEmpty(getAppConfigFile()) ? getAppConfigFile() : (StringUtils.isEmpty(getAppName()) ? ("application." + getConfigFile()) : (getAppName() + "." + getConfigFile())), environment);
            com.sq.common.config.Environment.getInstance().setExternalConfigMap(externalProperties);
            com.sq.common.config.Environment.getInstance().setAppExternalConfigMap(appExternalProperties);
        }
    }

    private Map<String, String> getConfigurations(String key, Environment environment) {
        Object rawProperties = environment.getProperty(key, Object.class);
        Map<String, String> externalProperties = new HashMap<>();
        try {
            if (rawProperties instanceof Map) {
                externalProperties.putAll((Map<String, String>) rawProperties);
            } else if (rawProperties instanceof String) {
                externalProperties.putAll(ConfigurationUtils.parseProperties((String) rawProperties));
            }

            if (environment instanceof ConfigurableEnvironment && externalProperties.isEmpty()) {
                ConfigurableEnvironment configurableEnvironment = (ConfigurableEnvironment) environment;
                PropertySource propertySource = configurableEnvironment.getPropertySources().get(key);
                if (propertySource != null) {
                    Object source = propertySource.getSource();
                    if (source instanceof Map) {
                        ((Map<String, Object>) source).forEach((k, v) -> {
                            externalProperties.put(k, (String) v);
                        });
                    }
                }
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        return externalProperties;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public Boolean getIncludeSpringEnv() {
        return includeSpringEnv;
    }

    public void setIncludeSpringEnv(Boolean includeSpringEnv) {
        this.includeSpringEnv = includeSpringEnv;
    }

    public ApplicationConfig getApplication() {
        return application;
    }

    public void setApplication(ApplicationConfig application) {
        this.application = application;
    }
}
