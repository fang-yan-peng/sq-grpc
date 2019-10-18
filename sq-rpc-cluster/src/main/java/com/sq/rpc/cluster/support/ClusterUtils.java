package com.sq.rpc.cluster.support;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.sq.common.Constants;
import com.sq.common.URL;
import com.sq.common.utils.StringUtils;

/**
 * ClusterUtils
 */
public class ClusterUtils {

    private ClusterUtils() {
    }

    public static URL mergeUrl(URL remoteUrl, Map<String, String> localMap) {
        Map<String, String> map = new HashMap<String, String>();
        Map<String, String> remoteMap = remoteUrl.getParameters();

        if (remoteMap != null && remoteMap.size() > 0) {
            map.putAll(remoteMap);

            // Remove configurations from provider, some items should be affected by provider.
            map.remove(Constants.THREAD_NAME_KEY);
            map.remove(Constants.DEFAULT_KEY_PREFIX + Constants.THREAD_NAME_KEY);

            map.remove(Constants.THREADPOOL_KEY);
            map.remove(Constants.DEFAULT_KEY_PREFIX + Constants.THREADPOOL_KEY);

            map.remove(Constants.CORE_THREADS_KEY);
            map.remove(Constants.DEFAULT_KEY_PREFIX + Constants.CORE_THREADS_KEY);

            map.remove(Constants.THREADS_KEY);
            map.remove(Constants.DEFAULT_KEY_PREFIX + Constants.THREADS_KEY);

            map.remove(Constants.QUEUES_KEY);
            map.remove(Constants.DEFAULT_KEY_PREFIX + Constants.QUEUES_KEY);

            map.remove(Constants.ALIVE_KEY);
            map.remove(Constants.DEFAULT_KEY_PREFIX + Constants.ALIVE_KEY);

            map.remove(Constants.TRANSPORTER_KEY);
            map.remove(Constants.DEFAULT_KEY_PREFIX + Constants.TRANSPORTER_KEY);

            map.remove(Constants.ASYNC_KEY);
            map.remove(Constants.DEFAULT_KEY_PREFIX + Constants.ASYNC_KEY);

            // remove method async entry.
            Set<String> methodAsyncKey = new HashSet<>();
            for (String key : map.keySet()) {
                if (key != null && key.endsWith("." + Constants.ASYNC_KEY)) {
                    methodAsyncKey.add(key);
                }
            }
            for (String needRemove : methodAsyncKey) {
                map.remove(needRemove);
            }
        }

        if (localMap != null && localMap.size() > 0) {
            // All providers come to here have been filtered by group, which means only those providers that have the exact same group value with the consumer could come to here.
            // So, generally, we don't need to care about the group value here.
            // But when comes to group merger, there is an exception, the consumer group may be '*' while the provider group can be empty or any other values.
            String remoteGroup = map.get(Constants.GROUP_KEY);
            String remoteRelease = map.get(Constants.RELEASE_KEY);
            map.putAll(localMap);
            if (StringUtils.isNotEmpty(remoteGroup)) {
                map.put(Constants.GROUP_KEY, remoteGroup);
            }
            // we should always keep the Provider RELEASE_KEY not overrode by the the value on Consumer side.
            map.remove(Constants.RELEASE_KEY);
            if (StringUtils.isNotEmpty(remoteRelease)) {
                map.put(Constants.RELEASE_KEY, remoteRelease);
            }
        }
        if (remoteMap != null && remoteMap.size() > 0) {
            // Use version passed from provider side
            reserveRemoteValue(Constants.GRPC_VERSION_KEY, map, remoteMap);
            reserveRemoteValue(Constants.VERSION_KEY, map, remoteMap);
            reserveRemoteValue(Constants.METHODS_KEY, map, remoteMap);
            reserveRemoteValue(Constants.TIMESTAMP_KEY, map, remoteMap);
            reserveRemoteValue(Constants.TAG_KEY, map, remoteMap);
            // TODO, for compatibility consideration, we cannot simply change the value behind APPLICATION_KEY from Consumer to Provider. So just add an extra key here.
            // Reserve application name from provider.
            map.put(Constants.REMOTE_APPLICATION_KEY, remoteMap.get(Constants.APPLICATION_KEY));

            // Combine filters and listeners on Provider and Consumer
            String remoteFilter = remoteMap.get(Constants.REFERENCE_FILTER_KEY);
            String localFilter = localMap.get(Constants.REFERENCE_FILTER_KEY);
            if (remoteFilter != null && remoteFilter.length() > 0
                    && localFilter != null && localFilter.length() > 0) {
                localMap.put(Constants.REFERENCE_FILTER_KEY, remoteFilter + "," + localFilter);
            }
            String remoteListener = remoteMap.get(Constants.INVOKER_LISTENER_KEY);
            String localListener = localMap.get(Constants.INVOKER_LISTENER_KEY);
            if (remoteListener != null && remoteListener.length() > 0
                    && localListener != null && localListener.length() > 0) {
                localMap.put(Constants.INVOKER_LISTENER_KEY, remoteListener + "," + localListener);
            }
        }

        return remoteUrl.clearParameters().addParameters(map);
    }

    private static void reserveRemoteValue(String key, Map<String, String> map, Map<String, String> remoteMap) {
        String remoteValue = remoteMap.get(key);
        if (StringUtils.isNotEmpty(remoteValue)) {
            map.put(key, remoteValue);
        }
    }

}