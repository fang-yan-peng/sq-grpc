package com.sq.config.spring.schema;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

import com.sq.common.Version;
import com.sq.config.ApplicationConfig;
import com.sq.config.ConsumerConfig;
import com.sq.config.MetadataReportConfig;
import com.sq.config.MetricsConfig;
import com.sq.config.ModuleConfig;
import com.sq.config.MonitorConfig;
import com.sq.config.ProtocolConfig;
import com.sq.config.ProviderConfig;
import com.sq.config.RegistryConfig;
import com.sq.config.spring.ConfigCenterBean;
import com.sq.config.spring.ReferenceBean;
import com.sq.config.spring.ServiceBean;

/**
 * SqGrpcNamespaceHandler
 *
 * @export
 */
public class SqGrpcNamespaceHandler extends NamespaceHandlerSupport {

    static {
        Version.checkDuplicate(SqGrpcNamespaceHandler.class);
    }

    @Override
    public void init() {
        registerBeanDefinitionParser("application", new SqGrpcBeanDefinitionParser(ApplicationConfig.class, true));
        registerBeanDefinitionParser("module", new SqGrpcBeanDefinitionParser(ModuleConfig.class, true));
        registerBeanDefinitionParser("registry", new SqGrpcBeanDefinitionParser(RegistryConfig.class, true));
        registerBeanDefinitionParser("config-center", new SqGrpcBeanDefinitionParser(ConfigCenterBean.class, true));
        registerBeanDefinitionParser("metadata-report", new SqGrpcBeanDefinitionParser(MetadataReportConfig.class, true));
        registerBeanDefinitionParser("monitor", new SqGrpcBeanDefinitionParser(MonitorConfig.class, true));
        registerBeanDefinitionParser("metrics", new SqGrpcBeanDefinitionParser(MetricsConfig.class, true));
        registerBeanDefinitionParser("provider", new SqGrpcBeanDefinitionParser(ProviderConfig.class, true));
        registerBeanDefinitionParser("consumer", new SqGrpcBeanDefinitionParser(ConsumerConfig.class, true));
        registerBeanDefinitionParser("protocol", new SqGrpcBeanDefinitionParser(ProtocolConfig.class, true));
        registerBeanDefinitionParser("service", new SqGrpcBeanDefinitionParser(ServiceBean.class, true));
        registerBeanDefinitionParser("reference", new SqGrpcBeanDefinitionParser(ReferenceBean.class, false));
        registerBeanDefinitionParser("annotation", new AnnotationBeanDefinitionParser());
    }

}
