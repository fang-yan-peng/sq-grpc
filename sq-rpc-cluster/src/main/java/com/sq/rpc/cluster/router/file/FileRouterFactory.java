package com.sq.rpc.cluster.router.file;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import com.sq.common.Constants;
import com.sq.common.URL;
import com.sq.common.URLBuilder;
import com.sq.common.utils.IOUtils;
import com.sq.rpc.cluster.Router;
import com.sq.rpc.cluster.RouterFactory;
import com.sq.rpc.cluster.router.script.ScriptRouterFactory;

public class FileRouterFactory implements RouterFactory {

    public static final String NAME = "file";

    private RouterFactory routerFactory;

    public void setRouterFactory(RouterFactory routerFactory) {
        this.routerFactory = routerFactory;
    }

    @Override
    public Router getRouter(URL url) {
        try {
            // Transform File URL into Script Route URL, and Load
            // file:///d:/path/to/route.js?router=script ==> script:///d:/path/to/route.js?type=js&rule=<file-content>
            String protocol = url.getParameter(Constants.ROUTER_KEY, ScriptRouterFactory.NAME); // Replace original protocol (maybe 'file') with 'script'
            String type = null; // Use file suffix to config script type, e.g., js, groovy ...
            String path = url.getPath();
            if (path != null) {
                int i = path.lastIndexOf('.');
                if (i > 0) {
                    type = path.substring(i + 1);
                }
            }
            String rule = IOUtils.read(new FileReader(new File(url.getAbsolutePath())));

            // FIXME: this code looks useless
            boolean runtime = url.getParameter(Constants.RUNTIME_KEY, false);
            URL script = URLBuilder.from(url)
                    .setProtocol(protocol)
                    .addParameter(Constants.TYPE_KEY, type)
                    .addParameter(Constants.RUNTIME_KEY, runtime)
                    .addParameterAndEncoded(Constants.RULE_KEY, rule)
                    .build();

            return routerFactory.getRouter(script);
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

}
