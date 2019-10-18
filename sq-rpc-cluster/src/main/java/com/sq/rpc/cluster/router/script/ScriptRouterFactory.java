package com.sq.rpc.cluster.router.script;


import com.sq.common.URL;
import com.sq.rpc.cluster.Router;
import com.sq.rpc.cluster.RouterFactory;

/**
 * ScriptRouterFactory
 * <p>
 * Example URLS used by Script Router Factory：
 * <ol>
 * <li> script://registryAddress?type=js&rule=xxxx
 * <li> script:///path/to/routerfile.js?type=js&rule=xxxx
 * <li> script://D:\path\to\routerfile.js?type=js&rule=xxxx
 * <li> script://C:/path/to/routerfile.js?type=js&rule=xxxx
 * </ol>
 * The host value in URL points out the address of the source content of the Script Router，Registry、File etc
 *
 */
public class ScriptRouterFactory implements RouterFactory {

    public static final String NAME = "script";

    @Override
    public Router getRouter(URL url) {
        return new ScriptRouter(url);
    }

}
