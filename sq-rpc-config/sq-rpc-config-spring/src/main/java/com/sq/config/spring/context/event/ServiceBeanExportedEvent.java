package com.sq.config.spring.context.event;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

import com.sq.config.spring.ServiceBean;

/**
 * A {@link ApplicationEvent} after {@link ServiceBean} {@link ServiceBean#export() export} invocation
 *
 * @see ApplicationEvent
 * @see ApplicationListener
 * @see ServiceBean
 * @since 2.6.5
 */
public class ServiceBeanExportedEvent extends ApplicationEvent {

    /**
     * Create a new ApplicationEvent.
     *
     * @param serviceBean {@link ServiceBean} bean
     */
    public ServiceBeanExportedEvent(ServiceBean serviceBean) {
        super(serviceBean);
    }

    /**
     * Get {@link ServiceBean} instance
     *
     * @return non-null
     */
    public ServiceBean getServiceBean() {
        return (ServiceBean) super.getSource();
    }
}
