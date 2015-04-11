package eecs3214a2.common;

import java.util.*;

/**
 * Interface for implementing
 * the logic to create a new agent
 * for a server or client service.
 * An instance of a factory is passed
 * to a server or client to spawn new
 * instances of the agent intended.
 *
 * @author Vincent Chu
 * @version 1.0
 */
public interface AgentFactory {

    /**
     * Returns a newly created
     * agent object with the given
     * context object initialized and
     * pre-populated as required by
     * agent's application.
     *
     * @param context
     *      session context object
     * @return
     *      newly created agent object
     */
    Agent createAgent(Map<String, Object> context);

} // AgentFactory
