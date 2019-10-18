package com.sq.rpc.cluster.router.script;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sq.common.Constants;
import com.sq.common.URL;
import com.sq.common.exceptions.RpcException;
import com.sq.common.utils.StringUtils;
import com.sq.rpc.Invocation;
import com.sq.rpc.Invoker;
import com.sq.rpc.RpcContext;
import com.sq.rpc.cluster.router.AbstractRouter;

/**
 * ScriptRouter
 */
public class ScriptRouter extends AbstractRouter {
    public static final String NAME = "SCRIPT_ROUTER";
    private static final int SCRIPT_ROUTER_DEFAULT_PRIORITY = 0;
    private static final Logger logger = LoggerFactory.getLogger(ScriptRouter.class);

    private static final Map<String, ScriptEngine> engines = new ConcurrentHashMap<>();

    private final ScriptEngine engine;

    private final String rule;

    private CompiledScript function;

    public ScriptRouter(URL url) {
        this.url = url;
        this.priority = url.getParameter(Constants.PRIORITY_KEY, SCRIPT_ROUTER_DEFAULT_PRIORITY);

        engine = getEngine(url);
        rule = getRule(url);
        try {
            Compilable compilable = (Compilable) engine;
            function = compilable.compile(rule);
        } catch (ScriptException e) {
            logger.error("route error, rule has been ignored. rule: " + rule +
                    ", url: " + RpcContext.getContext().getUrl(), e);
        }


    }

    /**
     * get rule from url parameters.
     */
    private String getRule(URL url) {
        String vRule = url.getParameterAndDecoded(Constants.RULE_KEY);
        if (StringUtils.isEmpty(vRule)) {
            throw new IllegalStateException("route rule can not be empty.");
        }
        return vRule;
    }

    /**
     * create ScriptEngine instance by type from url parameters, then cache it
     */
    private ScriptEngine getEngine(URL url) {
        String type = url.getParameter(Constants.TYPE_KEY, Constants.DEFAULT_SCRIPT_TYPE_KEY);

        return engines.computeIfAbsent(type, t -> {
            ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByName(type);
            if (scriptEngine == null) {
                throw new IllegalStateException("unsupported route engine type: " + type);
            }
            return scriptEngine;
        });
    }

    @Override
    public <T> List<Invoker<T>> route(List<Invoker<T>> invokers, URL url, Invocation invocation) throws RpcException {
        try {
            Bindings bindings = createBindings(invokers, invocation);
            if (function == null) {
                return invokers;
            }
            return getRoutedInvokers(function.eval(bindings));
        } catch (ScriptException e) {
            logger.error("route error, rule has been ignored. rule: " + rule + ", method:" +
                    invocation.getMethodName() + ", url: " + RpcContext.getContext().getUrl(), e);
            return invokers;
        }
    }

    /**
     * get routed invokers from result of script rule evaluation
     */
    @SuppressWarnings("unchecked")
    protected <T> List<Invoker<T>> getRoutedInvokers(Object obj) {
        if (obj instanceof Invoker[]) {
            return Arrays.asList((Invoker<T>[]) obj);
        } else if (obj instanceof Object[]) {
            return Arrays.stream((Object[]) obj).map(item -> (Invoker<T>) item).collect(Collectors.toList());
        } else {
            return (List<Invoker<T>>) obj;
        }
    }

    /**
     * create bindings for script engine
     */
    private <T> Bindings createBindings(List<Invoker<T>> invokers, Invocation invocation) {
        Bindings bindings = engine.createBindings();
        // create a new List of invokers
        bindings.put("invokers", new ArrayList<>(invokers));
        bindings.put("invocation", invocation);
        bindings.put("context", RpcContext.getContext());
        return bindings;
    }

    @Override
    public boolean isRuntime() {
        return this.url.getParameter(Constants.RUNTIME_KEY, false);
    }

    @Override
    public boolean isForce() {
        return url.getParameter(Constants.FORCE_KEY, false);
    }

}
