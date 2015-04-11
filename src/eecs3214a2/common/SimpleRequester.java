package eecs3214a2.common;

import java.io.*;
import java.util.*;

/**
 * Abstract simple requester.
 * Send a message as returned by
 * abstract method makeRequest, and
 * then close the connection
 * immediately.
 *
 * @author Vincent Chu
 * @version 1.0
 */
public abstract class SimpleRequester extends Requester {

    /**
     * Handles the incoming received messages
     * and response via the writer provided to
     * sent outgoing message. Context is also
     * passed to given access to required data
     * structures and values in memory.
     *
     * @param context
     *      the saved objects, session context
     * @param received
     *      the receiving message
     * @param sender
     *      the outgoing message sender
     * @return
     *      true if should invoke next agent
     *      when responding message arrives
     *      from remote agent, or false if
     *      should close the connection.
     * @throws IOException
     *      if IO error occurs while reading
     *      from or writing to socket
     *      connection.
     */
    @Override
    public boolean doAction(Map<String, Object> context,
                            String received,
                            PrintWriter sender) throws IOException {
        context.putAll(mContext);
        if (received == null) {
            sender.println(makeRequest(context));
        }
        return false;
    }

    /**
     * Abstract method that returns the
     * request message to send to the server.
     *
     * @param context
     *      the saved objects, session context
     * @return
     *      request message to send
     */
    protected abstract String makeRequest(Map<String, Object> context);

} // SimpleRequester
