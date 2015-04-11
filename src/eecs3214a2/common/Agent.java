package eecs3214a2.common;

import java.io.*;
import java.util.*;

/**
 * Interface for implementing an agent,
 * either the client or the server. The
 * agent is class that handles the actions
 * performed or the message sent given a
 * received message.
 *
 * @author Vincent Chu
 * @version 1.0
 */
public interface Agent {

    /**
     * Handles the incoming received messages
     * and response via the writer provided to
     * sent outgoing message. Context is also
     * passed to given access to required data
     * structures and values in memory.
     *
     * @param context
     *      the saved objects, session context
     * @param message
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
    boolean doAction(Map<String, Object> context,
                     String message,
                     PrintWriter sender) throws IOException;

} // Agent
