package com.sq.config.builders;


import com.sq.common.utils.StringUtils;
import com.sq.config.AbstractReferenceConfig;

/**
 * AbstractBuilder
 *
 * @since 2.7
 */
public abstract class AbstractReferenceBuilder<T extends AbstractReferenceConfig, B extends AbstractReferenceBuilder<T, B>>
        extends AbstractInterfaceBuilder<T, B> {

    /**
     * Check if services provider exists, if not exists, it will be fast fail
     */
    protected Boolean check;

    /**
     * Whether to eagle-init
     */
    protected Boolean init;

    /**
     * Whether to use generic interface
     */
    protected String generic;

    /**
     * Whether to find reference's instance from the current JVM
     */
    protected Boolean injvm;

    /**
     * Lazy create connection
     */
    protected Boolean lazy;

    protected String reconnect;

    protected Boolean sticky;

    /**
     * The remote services version the customer side will reference
     */
    protected String version;

    /**
     * The remote services group the customer side will reference
     */
    protected String group;

    public B check(Boolean check) {
        this.check = check;
        return getThis();
    }

    public B init(Boolean init) {
        this.init = init;
        return getThis();
    }

    public B generic(String generic) {
        this.generic = generic;
        return getThis();
    }

    public B generic(Boolean generic) {
        if (generic != null) {
            this.generic = generic.toString();
        } else {
            this.generic = null;
        }
        return getThis();
    }

    /**
     * @param injvm
     * @see AbstractInterfaceBuilder#scope(String)
     * @deprecated instead, use the parameter <b>scope</b> to judge if it's in jvm, scope=local
     */
    @Deprecated
    public B injvm(Boolean injvm) {
        this.injvm = injvm;
        return getThis();
    }

    public B lazy(Boolean lazy) {
        this.lazy = lazy;
        return getThis();
    }

    public B reconnect(String reconnect) {
        this.reconnect = reconnect;
        return getThis();
    }

    public B sticky(Boolean sticky) {
        this.sticky = sticky;
        return getThis();
    }

    public B version(String version) {
        this.version = version;
        return getThis();
    }

    public B group(String group) {
        this.group = group;
        return getThis();
    }

    @Override
    public void build(T instance) {
        super.build(instance);

        if (check != null) {
            instance.setCheck(check);
        }
        if (init != null) {
            instance.setInit(init);
        }
        if (!StringUtils.isEmpty(generic)) {
            instance.setGeneric(generic);
        }
        if (injvm != null) {
            instance.setInjvm(injvm);
        }
        if (lazy != null) {
            instance.setLazy(lazy);
        }
        if (!StringUtils.isEmpty(reconnect)) {
            instance.setReconnect(reconnect);
        }
        if (sticky != null) {
            instance.setSticky(sticky);
        }
        if (!StringUtils.isEmpty(version)) {
            instance.setVersion(version);
        }
        if (!StringUtils.isEmpty(group)) {
            instance.setGroup(group);
        }
    }
}
