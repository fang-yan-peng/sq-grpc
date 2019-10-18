package com.sq.rpc.support;

import java.util.HashMap;
import java.util.Map;

import com.sq.common.utils.StringUtils;
import com.sq.rpc.Result;


/**
 *
 */
public abstract class AbstractResult implements Result {
    protected Map<String, String> attachments = new HashMap<String, String>();

    protected Object result;

    protected Throwable exception;

    @Override
    public Map<String, String> getAttachments() {
        return attachments;
    }

    @Override
    public void setAttachments(Map<String, String> map) {
        this.attachments = map == null ? new HashMap<String, String>() : map;
    }

    @Override
    public void addAttachments(Map<String, String> map) {
        if (map == null) {
            return;
        }
        if (this.attachments == null) {
            this.attachments = new HashMap<String, String>();
        }
        this.attachments.putAll(map);
    }

    @Override
    public String getAttachment(String key) {
        return attachments.get(key);
    }

    @Override
    public String getAttachment(String key, String defaultValue) {
        String result = attachments.get(key);
        if (StringUtils.isEmpty(result)) {
            result = defaultValue;
        }
        return result;
    }

    @Override
    public void setAttachment(String key, String value) {
        attachments.put(key, value);
    }

}
