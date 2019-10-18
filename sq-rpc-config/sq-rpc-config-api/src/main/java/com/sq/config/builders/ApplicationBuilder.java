/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sq.config.builders;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.sq.common.Constants;
import com.sq.common.utils.StringUtils;
import com.sq.config.ApplicationConfig;
import com.sq.config.MonitorConfig;
import com.sq.config.RegistryConfig;

/**
 * This is a builder for build {@link ApplicationConfig}.
 * @since 2.7
 */
public class ApplicationBuilder extends AbstractBuilder<ApplicationConfig, ApplicationBuilder> {
    private String name;
    private String version;
    private String owner;
    private String organization;
    private String architecture;
    private String environment = Constants.PRODUCTION_ENVIRONMENT;
    private String compiler;
    private String logger;
    private List<RegistryConfig> registries;
    private String registryIds;
    private MonitorConfig monitor;
    private Boolean isDefault;
    private String dumpDirectory;
    private Boolean qosEnable;
    private Integer qosPort;
    private Boolean qosAcceptForeignIp;
    private Map<String, String> parameters;
    private String shutwait;

    public ApplicationBuilder name(String name) {
        this.name = name;
        return getThis();
    }

    public ApplicationBuilder version(String version) {
        this.version = version;
        return getThis();
    }

    public ApplicationBuilder owner(String owner) {
        this.owner = owner;
        return getThis();
    }

    public ApplicationBuilder organization(String organization) {
        this.organization = organization;
        return getThis();
    }

    public ApplicationBuilder architecture(String architecture) {
        this.architecture = architecture;
        return getThis();
    }

    public ApplicationBuilder environment(String environment) {
        this.environment = environment;
        return getThis();
    }

    public ApplicationBuilder compiler(String compiler) {
        this.compiler = compiler;
        return getThis();
    }

    public ApplicationBuilder logger(String logger) {
        this.logger = logger;
        return getThis();
    }

    public ApplicationBuilder addRegistry(RegistryConfig registry) {
        if (this.registries == null) {
            this.registries = new ArrayList<>();
        }
        this.registries.add(registry);
        return getThis();
    }

    public ApplicationBuilder addRegistries(List<? extends RegistryConfig> registries) {
        if (this.registries == null) {
            this.registries = new ArrayList<>();
        }
        this.registries.addAll(registries);
        return getThis();
    }

    public ApplicationBuilder registryIds(String registryIds) {
        this.registryIds = registryIds;
        return getThis();
    }

    public ApplicationBuilder monitor(MonitorConfig monitor) {
        this.monitor = monitor;
        return getThis();
    }

    public ApplicationBuilder monitor(String monitor) {
        this.monitor = new MonitorConfig(monitor);
        return getThis();
    }

    public ApplicationBuilder isDefault(Boolean isDefault) {
        this.isDefault = isDefault;
        return getThis();
    }

    public ApplicationBuilder dumpDirectory(String dumpDirectory) {
        this.dumpDirectory = dumpDirectory;
        return getThis();
    }

    public ApplicationBuilder qosEnable(Boolean qosEnable) {
        this.qosEnable = qosEnable;
        return getThis();
    }

    public ApplicationBuilder qosPort(Integer qosPort) {
        this.qosPort = qosPort;
        return getThis();
    }

    public ApplicationBuilder qosAcceptForeignIp(Boolean qosAcceptForeignIp) {
        this.qosAcceptForeignIp = qosAcceptForeignIp;
        return getThis();
    }

    public ApplicationBuilder shutwait(String shutwait) {
        this.shutwait = shutwait;
        return getThis();
    }

    public ApplicationBuilder appendParameter(String key, String value) {
        this.parameters = appendParameter(parameters, key, value);
        return getThis();
    }

    public ApplicationBuilder appendParameters(Map<String, String> appendParameters) {
        this.parameters = appendParameters(parameters, appendParameters);
        return getThis();
    }

    public ApplicationConfig build() {
        ApplicationConfig config = new ApplicationConfig();
        super.build(config);

        config.setName(name);
        config.setVersion(this.version);
        config.setOwner(this.owner);
        config.setOrganization(this.organization);
        config.setArchitecture(this.architecture);
        config.setEnvironment(this.environment);
        config.setCompiler(this.compiler);
        config.setLogger(this.logger);
        config.setRegistries(this.registries);
        config.setRegistryIds(this.registryIds);
        config.setMonitor(this.monitor);
        config.setDefault(this.isDefault);
        config.setDumpDirectory(this.dumpDirectory);
        config.setQosEnable(this.qosEnable);
        config.setQosPort(this.qosPort);
        config.setQosAcceptForeignIp(this.qosAcceptForeignIp);
        config.setParameters(this.parameters);
        if (!StringUtils.isEmpty(shutwait)) {
            config.setShutwait(shutwait);
        }
        return config;
    }

    @Override
    protected ApplicationBuilder getThis() {
        return this;
    }
}
