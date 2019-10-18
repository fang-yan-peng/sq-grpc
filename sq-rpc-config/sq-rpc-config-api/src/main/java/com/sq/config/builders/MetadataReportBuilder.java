package com.sq.config.builders;

import java.util.Map;

import com.sq.config.MetadataReportConfig;


/**
 * This is a builder for build {@link MetadataReportConfig}.
 *
 * @since 2.7
 */
public class MetadataReportBuilder extends AbstractBuilder<MetadataReportConfig, MetadataReportBuilder> {

    // Register center address
    private String address;

    // Username to login register center
    private String username;

    // Password to login register center
    private String password;

    // Request timeout in milliseconds for register center
    private Integer timeout;

    /**
     * The group the metadata in . It is the same as registry
     */
    private String group;

    // Customized parameters
    private Map<String, String> parameters;

    private Integer retryTimes;

    private Integer retryPeriod;
    /**
     * By default the metadatastore will store full metadata repeatly every day .
     */
    private Boolean cycleReport;

    /**
     * Sync report, default async
     */
    private Boolean syncReport;

    public MetadataReportBuilder address(String address) {
        this.address = address;
        return getThis();
    }

    public MetadataReportBuilder username(String username) {
        this.username = username;
        return getThis();
    }

    public MetadataReportBuilder password(String password) {
        this.password = password;
        return getThis();
    }

    public MetadataReportBuilder timeout(Integer timeout) {
        this.timeout = timeout;
        return getThis();
    }

    public MetadataReportBuilder group(String group) {
        this.group = group;
        return getThis();
    }

    public MetadataReportBuilder appendParameters(Map<String, String> appendParameters) {
        this.parameters = appendParameters(this.parameters, appendParameters);
        return getThis();
    }

    public MetadataReportBuilder appendParameter(String key, String value) {
        this.parameters = appendParameter(this.parameters, key, value);
        return getThis();
    }

    public MetadataReportBuilder retryTimes(Integer retryTimes) {
        this.retryTimes = retryTimes;
        return getThis();
    }

    public MetadataReportBuilder retryPeriod(Integer retryPeriod) {
        this.retryPeriod = retryPeriod;
        return getThis();
    }

    public MetadataReportBuilder cycleReport(Boolean cycleReport) {
        this.cycleReport = cycleReport;
        return getThis();
    }

    public MetadataReportBuilder syncReport(Boolean syncReport) {
        this.syncReport = syncReport;
        return getThis();
    }

    public MetadataReportConfig build() {
        MetadataReportConfig metadataReport = new MetadataReportConfig();
        super.build(metadataReport);

        metadataReport.setAddress(address);
        metadataReport.setUsername(username);
        metadataReport.setPassword(password);
        metadataReport.setTimeout(timeout);
        metadataReport.setGroup(group);
        metadataReport.setParameters(parameters);
        metadataReport.setRetryTimes(retryTimes);
        metadataReport.setRetryPeriod(retryPeriod);
        metadataReport.setCycleReport(cycleReport);
        metadataReport.setSyncReport(syncReport);

        return metadataReport;
    }

    @Override
    protected MetadataReportBuilder getThis() {
        return this;
    }
}
