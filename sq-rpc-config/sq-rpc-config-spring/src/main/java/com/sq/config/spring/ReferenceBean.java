package com.sq.config.spring;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.sq.common.Constants;
import com.sq.common.utils.CollectionUtils;
import com.sq.common.utils.StringUtils;
import com.sq.config.ApplicationConfig;
import com.sq.config.ConfigCenterConfig;
import com.sq.config.ConsumerConfig;
import com.sq.config.MetadataReportConfig;
import com.sq.config.MetricsConfig;
import com.sq.config.ModuleConfig;
import com.sq.config.MonitorConfig;
import com.sq.config.ReferenceConfig;
import com.sq.config.RegistryConfig;
import com.sq.config.annotation.Reference;
import com.sq.config.spring.extension.SpringExtensionFactory;
import com.sq.config.support.Parameter;

/**
 * ReferenceFactoryBean
 */
public class ReferenceBean<T> extends ReferenceConfig<T> implements FactoryBean, ApplicationContextAware, InitializingBean, DisposableBean {

    private static final long serialVersionUID = 213195494150089726L;

    private transient ApplicationContext applicationContext;

    public ReferenceBean() {
        super();
    }

    public ReferenceBean(Reference reference) {
        super(reference);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        SpringExtensionFactory.addApplicationContext(applicationContext);
    }

    @Override
    public Object getObject() {
        return get();
    }

    @Override
    public Class<?> getObjectType() {
        return getInterfaceClass();
    }

    @Override
    @Parameter(excluded = true)
    public boolean isSingleton() {
        return true;
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public void afterPropertiesSet() throws Exception {
        if (applicationContext != null) {
            BeanFactoryUtils.beansOfTypeIncludingAncestors(applicationContext, ConfigCenterBean.class, false, false);
        }

        if (getConsumer() == null) {
            Map<String, ConsumerConfig> consumerConfigMap = applicationContext == null ? null : BeanFactoryUtils.beansOfTypeIncludingAncestors(applicationContext, ConsumerConfig.class, false, false);
            if (consumerConfigMap != null && consumerConfigMap.size() > 0) {
                ConsumerConfig consumerConfig = null;
                for (ConsumerConfig config : consumerConfigMap.values()) {
                    if (config.isDefault() == null || config.isDefault()) {
                        if (consumerConfig != null) {
                            throw new IllegalStateException("Duplicate consumer configs: " + consumerConfig + " and " + config);
                        }
                        consumerConfig = config;
                    }
                }
                if (consumerConfig != null) {
                    setConsumer(consumerConfig);
                }
            }
        }
        if (getApplication() == null
                && (getConsumer() == null || getConsumer().getApplication() == null)) {
            Map<String, ApplicationConfig> applicationConfigMap = applicationContext == null ? null : BeanFactoryUtils.beansOfTypeIncludingAncestors(applicationContext, ApplicationConfig.class, false, false);
            if (applicationConfigMap != null && applicationConfigMap.size() > 0) {
                ApplicationConfig applicationConfig = null;
                for (ApplicationConfig config : applicationConfigMap.values()) {
                    if (applicationConfig != null) {
                        throw new IllegalStateException("Duplicate application configs: " + applicationConfig + " and " + config);
                    }
                    applicationConfig = config;
                }
                if (applicationConfig != null) {
                    setApplication(applicationConfig);
                }
            }
        }
        if (getModule() == null
                && (getConsumer() == null || getConsumer().getModule() == null)) {
            Map<String, ModuleConfig> moduleConfigMap = applicationContext == null ? null : BeanFactoryUtils.beansOfTypeIncludingAncestors(applicationContext, ModuleConfig.class, false, false);
            if (moduleConfigMap != null && moduleConfigMap.size() > 0) {
                ModuleConfig moduleConfig = null;
                for (ModuleConfig config : moduleConfigMap.values()) {
                    if (config.isDefault() == null || config.isDefault()) {
                        if (moduleConfig != null) {
                            throw new IllegalStateException("Duplicate module configs: " + moduleConfig + " and " + config);
                        }
                        moduleConfig = config;
                    }
                }
                if (moduleConfig != null) {
                    setModule(moduleConfig);
                }
            }
        }

        if (StringUtils.isEmpty(getRegistryIds())) {
            if (getApplication() != null && StringUtils.isNotEmpty(getApplication().getRegistryIds())) {
                setRegistryIds(getApplication().getRegistryIds());
            }
            if (getConsumer() != null && StringUtils.isNotEmpty(getConsumer().getRegistryIds())) {
                setRegistryIds(getConsumer().getRegistryIds());
            }
        }

        if (CollectionUtils.isEmpty(getRegistries())
                && (getConsumer() == null || CollectionUtils.isEmpty(getConsumer().getRegistries()))
                && (getApplication() == null || CollectionUtils.isEmpty(getApplication().getRegistries()))) {
            Map<String, RegistryConfig> registryConfigMap = applicationContext == null ? null : BeanFactoryUtils.beansOfTypeIncludingAncestors(applicationContext, RegistryConfig.class, false, false);
            if (registryConfigMap != null && registryConfigMap.size() > 0) {
                List<RegistryConfig> registryConfigs = new ArrayList<>();
                if (StringUtils.isNotEmpty(registryIds)) {
                    Arrays.stream(Constants.COMMA_SPLIT_PATTERN.split(registryIds)).forEach(id -> {
                        if (registryConfigMap.containsKey(id)) {
                            registryConfigs.add(registryConfigMap.get(id));
                        }
                    });
                }

                if (registryConfigs.isEmpty()) {
                    for (RegistryConfig config : registryConfigMap.values()) {
                        if (StringUtils.isEmpty(registryIds)) {
                            registryConfigs.add(config);
                        }
                    }
                }
                if (!registryConfigs.isEmpty()) {
                    super.setRegistries(registryConfigs);
                }
            }
        }

        if (getMetadataReportConfig() == null) {
            Map<String, MetadataReportConfig> metadataReportConfigMap = applicationContext == null ? null : BeanFactoryUtils.beansOfTypeIncludingAncestors(applicationContext, MetadataReportConfig.class, false, false);
            if (metadataReportConfigMap != null && metadataReportConfigMap.size() == 1) {
                // first elements
                super.setMetadataReportConfig(metadataReportConfigMap.values().iterator().next());
            } else if (metadataReportConfigMap != null && metadataReportConfigMap.size() > 1) {
                throw new IllegalStateException("Multiple MetadataReport configs: " + metadataReportConfigMap);
            }
        }

        if (getConfigCenter() == null) {
            Map<String, ConfigCenterConfig> configenterMap = applicationContext == null ? null : BeanFactoryUtils.beansOfTypeIncludingAncestors(applicationContext, ConfigCenterConfig.class, false, false);
            if (configenterMap != null && configenterMap.size() == 1) {
                super.setConfigCenter(configenterMap.values().iterator().next());
            } else if (configenterMap != null && configenterMap.size() > 1) {
                throw new IllegalStateException("Multiple ConfigCenter found:" + configenterMap);
            }
        }

        if (getMonitor() == null
                && (getConsumer() == null || getConsumer().getMonitor() == null)
                && (getApplication() == null || getApplication().getMonitor() == null)) {
            Map<String, MonitorConfig> monitorConfigMap = applicationContext == null ? null : BeanFactoryUtils.beansOfTypeIncludingAncestors(applicationContext, MonitorConfig.class, false, false);
            if (monitorConfigMap != null && monitorConfigMap.size() > 0) {
                MonitorConfig monitorConfig = null;
                for (MonitorConfig config : monitorConfigMap.values()) {
                    if (config.isDefault() == null || config.isDefault()) {
                        if (monitorConfig != null) {
                            throw new IllegalStateException("Duplicate monitor configs: " + monitorConfig + " and " + config);
                        }
                        monitorConfig = config;
                    }
                }
                if (monitorConfig != null) {
                    setMonitor(monitorConfig);
                }
            }
        }

        if (getMetrics() == null) {
            Map<String, MetricsConfig> metricsConfigMap = applicationContext == null ? null : BeanFactoryUtils.beansOfTypeIncludingAncestors(applicationContext, MetricsConfig.class, false, false);
            if (metricsConfigMap != null && metricsConfigMap.size() > 0) {
                MetricsConfig metricsConfig = null;
                for (MetricsConfig config : metricsConfigMap.values()) {
                    if (metricsConfig != null) {
                        throw new IllegalStateException("Duplicate metrics configs: " + metricsConfig + " and " + config);
                    }
                    metricsConfig = config;
                }
                if (metricsConfig != null) {
                    setMetrics(metricsConfig);
                }
            }
        }

        if (shouldInit()) {
            getObject();
        }
    }

    @Override
    public void destroy() {
        // do nothing
    }
}
