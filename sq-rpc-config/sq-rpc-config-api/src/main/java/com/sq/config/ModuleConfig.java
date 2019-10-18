package com.sq.config;

import java.util.ArrayList;
import java.util.List;

import com.sq.common.Constants;
import com.sq.common.utils.CollectionUtils;
import com.sq.common.utils.StringUtils;
import com.sq.config.support.Parameter;

/**
 * The module info
 *
 * @export
 */
public class ModuleConfig extends AbstractConfig {

    private static final long serialVersionUID = 5508512956753757169L;

    /**
     * Module name
     */
    private String name;

    /**
     * Module version
     */
    private String version;

    /**
     * Module owner
     */
    private String owner;

    /**
     * Module's organization
     */
    private String organization;

    /**
     * Registry centers
     */
    private List<RegistryConfig> registries;

    /**
     * Monitor center
     */
    private MonitorConfig monitor;

    /**
     * If it's default
     */
    private Boolean isDefault;

    public ModuleConfig() {
    }

    public ModuleConfig(String name) {
        setName(name);
    }

    @Parameter(key = "module", required = true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        checkName(Constants.NAME, name);
        this.name = name;
        if (StringUtils.isEmpty(id)) {
            id = name;
        }
    }

    @Parameter(key = "module.version")
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        checkName(Constants.OWNER, owner);
        this.owner = owner;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        checkName(Constants.ORGANIZATION, organization);
        this.organization = organization;
    }

    public RegistryConfig getRegistry() {
        return CollectionUtils.isEmpty(registries) ? null : registries.get(0);
    }

    public void setRegistry(RegistryConfig registry) {
        List<RegistryConfig> registries = new ArrayList<RegistryConfig>(1);
        registries.add(registry);
        this.registries = registries;
    }

    public List<RegistryConfig> getRegistries() {
        return registries;
    }

    @SuppressWarnings({"unchecked"})
    public void setRegistries(List<? extends RegistryConfig> registries) {
        this.registries = (List<RegistryConfig>) registries;
    }

    public MonitorConfig getMonitor() {
        return monitor;
    }

    public void setMonitor(MonitorConfig monitor) {
        this.monitor = monitor;
    }

    public void setMonitor(String monitor) {
        this.monitor = new MonitorConfig(monitor);
    }

    public Boolean isDefault() {
        return isDefault;
    }

    public void setDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

}
