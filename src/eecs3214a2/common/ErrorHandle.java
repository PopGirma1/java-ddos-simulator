package eecs3214a2.common;

/**
 * Interface for implementing
 * error handling mechanism.
 *
 * @author Vincent Chu
 * @version 1.0
 */
public interface ErrorHandle {

    /**
     * Method called when an error occurs.
     *
     * @param e
     *      thrown exception object
     */
    public void doError(Throwable e);

} // ErrorHandle
