package eecs3214a2.common;

import java.util.*;

/**
 * Abstract class that implements a
 * request agent to sends the request
 * to a server. Allows adding context
 * specific for each request.
 *
 * @author Vincent Chu
 * @version 1.0
 */
public abstract class Requester implements Agent {

    protected Map<String, Object> mContext = new HashMap<>();

    /**
     * Associates the specified value with the specified
     * key in the requester context. If the context previously
     * contained a mapping for the key, the old value is
     * replaced by the specified value.
     *
     * @param key
     *      key with which the specified value is to be associated
     * @param value
     *      value to be associated with the specified key
     * @return
     *      this requester object
     */
    public Requester put(String key, Object value) {
        mContext.put(key, value);
        return this;
    }

    /**
     * Copies all of the mappings from the specified context
     * to this requester context.
     *
     * @param context
     *      value to be associated with the specified key
     * @return
     *      this requester object
     */
    public Requester putAll(Map<String, Object> context) {
        mContext.putAll(context);
        return this;
    }

    /**
     * Reset the context. Removes all of the
     * mappings from the context.
     *
     * @return
     *      this requester object
     */
    public Requester reset() {
        mContext.clear();
        return this;
    }

} // Requester
