package com.sq.rpc.cluster.router;

/**
 * TODO Extract more code here if necessary
 */
public abstract class AbstractRouterRule {
    private String rawRule;
    private boolean runtime = true;
    private boolean force = false;
    private boolean valid = true;
    private boolean enabled = true;
    private int priority;
    private boolean dynamic = false;

    private String scope;
    private String key;

    public String getRawRule() {
        return rawRule;
    }

    public void setRawRule(String rawRule) {
        this.rawRule = rawRule;
    }

    public boolean isRuntime() {
        return runtime;
    }

    public void setRuntime(boolean runtime) {
        this.runtime = runtime;
    }

    public boolean isForce() {
        return force;
    }

    public void setForce(boolean force) {
        this.force = force;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public boolean isDynamic() {
        return dynamic;
    }

    public void setDynamic(boolean dynamic) {
        this.dynamic = dynamic;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
