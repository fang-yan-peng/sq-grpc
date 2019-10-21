package com.sq.config.spring.context.config;

import javax.servlet.ServletContextEvent;

import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;

import com.sq.common.Constants;
import com.sq.common.extension.ExtensionLoader;
import com.sq.rpc.Protocol;

/**
 * 在spring 容器启动后，开启grpc协议。
 * @author yanpengfang
 * create 2019-10-21 9:54 AM
 */
public class SqContextLoaderListener extends ContextLoaderListener {

    public SqContextLoaderListener() {
        super();
    }

    public SqContextLoaderListener(WebApplicationContext context) {
        super(context);
    }

    @Override
    public void contextInitialized(ServletContextEvent event) {
        super.contextInitialized(event);
        if (ExtensionLoader.getExtensionLoader(Protocol.class)
                .hasExtension(Constants.SQ_RPC_PROTOCOL)) {
            ExtensionLoader.getExtensionLoader(Protocol.class)
                    .getExtension(Constants.SQ_RPC_PROTOCOL).start();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        super.contextDestroyed(event);
    }
}
