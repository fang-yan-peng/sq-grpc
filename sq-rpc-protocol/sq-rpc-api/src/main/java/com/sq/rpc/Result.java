package com.sq.rpc;

import java.io.Serializable;
import java.util.Map;


/**
 * RPC invoke result. (API, Prototype, NonThreadSafe)
 *
 * @serial Don't change the class name and package name.
 */
public interface Result extends Serializable {

    /**
     * Get invoke result.
     *
     * @return result. if no result return null.
     */
    Object getValue();

    /**
     * Get exception.
     *
     * @return exception. if no exception return null.
     */
    Throwable getException();

    /**
     * Has exception.
     *
     * @return has exception.
     */
    boolean hasException();

    /**
     * Recreate.
     * <p>
     * <code>
     * if (hasException()) {
     * throw getException();
     * } else {
     * return getValue();
     * }
     * </code>
     *
     * @return result.
     * @throws if has exception throw it.
     */
    Object recreate() throws Throwable;

    /**
     * get attachments.
     *
     * @return attachments.
     */
    Map<String, String> getAttachments();

    /**
     * Add the specified map to existing attachments in this instance.
     *
     * @param map
     */
    void addAttachments(Map<String, String> map);

    /**
     * Replace the existing attachments with the specified param.
     *
     * @param map
     */
    void setAttachments(Map<String, String> map);

    /**
     * get attachment by key.
     *
     * @return attachment value.
     */
    String getAttachment(String key);

    /**
     * get attachment by key with default value.
     *
     * @return attachment value.
     */
    String getAttachment(String key, String defaultValue);

    void setAttachment(String key, String value);

}