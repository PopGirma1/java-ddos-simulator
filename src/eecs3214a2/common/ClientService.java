package eecs3214a2.common;

import java.net.*;
import java.io.*;
import java.util.*;

/**
 * Generic implementation of a
 * client service with mechanism for
 * sending requests and handling
 * responses.
 *
 * @author Vincent Chu
 * @version 1.0
 */
public class ClientService extends Thread {

    private static final Log LOG = Log.get();

    /**
     * Server host name
     * Server port number
     * Client agent
     * Error handling mechanism
     */
    private String host;
    private int port;
    private Agent clientAgent;
    private ErrorHandle errorHandle;

    /**
     * Send a request to the given host name
     * and port number with an agent created by
     * the factory given. Spawn a thread to handle
     * the request and response. Non-blocking.
     *
     * @param host
     *      server host name
     * @param port
     *      server port number
     * @param clientAgent
     *      the client agent
     * @param errorHandle
     *      the error handling mechanism
     */
    private void sendRequest(String host, int port, Agent clientAgent, ErrorHandle errorHandle) {
        this.host = host;
        this.port = port;
        this.clientAgent = clientAgent;
        this.errorHandle = (errorHandle == null) ? new SimpleClientErrorHandle() : errorHandle;
        this.start();
    }

    /**
     * Send a request to the given host name
     * and port number with an agent created by
     * the factory given. Spawn a thread to handle
     * the request and response. Non-blocking.
     *
     * @param host
     *      server host name
     * @param port
     *      server port number
     * @param clientAgent
     *      the client agent
     */
    private void sendRequest(String host, int port, Agent clientAgent) {
        sendRequest(host, port, clientAgent, null);
    }

    /**
     * Send a request to the given host name
     * and port number with given the message.
     * Spawn a thread to handle the request and
     * response. Non-blocking.
     *
     * @param host
     *      server host name
     * @param port
     *      server port number
     * @param message
     *      request message to send
     * @param errorHandle
     *      the error handling mechanism
     */
    private void sendRequest(String host, int port, final String message, ErrorHandle errorHandle) {
        sendRequest(host, port, new SimpleRequester() {
            @Override
            protected String makeRequest(Map<String, Object> context) {
                LOG.info("to server: " + message);
                return message;
            }
        }, errorHandle);
    }

    /**
     * Send a request to the given host name
     * and port number with given the message.
     * Spawn a thread to handle the request and
     * response. Non-blocking.
     *
     * @param host
     *      server host name
     * @param port
     *      server port number
     * @param message
     *      request message to send
     */
    private void sendRequest(String host, int port, final String message) {
        sendRequest(host, port, message, null);
    }

    @Override
    public void run() {
        try (Socket socket = new Socket(host, port);
             InputStream in = socket.getInputStream();
             OutputStream out = socket.getOutputStream();
             InputStreamReader iReader = new InputStreamReader(in);
             BufferedReader reader = new BufferedReader(iReader);
             PrintWriter writer = new PrintWriter(out, true);
        ) {
            LOG.verb("request opened");
            Map<String, Object> context = new HashMap<>();

            // Populated connection context
            InetAddress local  = socket.getLocalAddress();
            InetAddress remote = socket.getInetAddress();
            context.put("LOCAL_ADDR", local.getHostAddress());
            context.put("LOCAL_HOST" , local.getHostName());
            context.put("LOCAL_PORT" , socket.getLocalPort());
            context.put("REMOTE_ADDR", remote.getHostAddress());
            context.put("REMOTE_HOST", remote.getHostName());
            context.put("REMOTE_PORT", socket.getPort());

            if (clientAgent.doAction(context, null, writer)) {
                for (;;) {
                    String message = reader.readLine();
                    LOG.info("from server: " + message);
                    if (message == null) {
                        break;
                    } else if (!clientAgent.doAction(context, message, writer)) {
                        break;
                    }
                }
            }
            LOG.verb("request closed");
        } catch (Exception e) {
            errorHandle.doError(e);
        }
    }

    // Static

    /**
     * Send a request to the given host name
     * and port number with an agent created by
     * the factory given. Spawn a thread to handle
     * the request and response. Non-blocking.
     *
     * @param host
     *      server host name
     * @param port
     *      server port number
     * @param clientAgent
     *      the client agent
     * @param errorHandle
     *      the error handling mechanism
     */
    public static void send(String host, int port, ErrorHandle errorHandle, Agent clientAgent) {
        (new ClientService()).sendRequest(host, port, clientAgent, errorHandle);
    }

    /**
     * Send a request to the given host name
     * and port number with given the message.
     * Spawn a thread to handle the request and
     * response. Non-blocking.
     *
     * @param host
     *      server host name
     * @param port
     *      server port number
     * @param errorHandle
     *      the error handling mechanism
     * @param message
     *      request message to send
     */
    public static void send(String host, int port, ErrorHandle errorHandle, String message) {
        (new ClientService()).sendRequest(host, port, message, errorHandle);
    }

    /**
     * Send a request to the given host name
     * and port number with given the message.
     * Spawn a thread to handle the request and
     * response. Non-blocking.
     *
     * @param host
     *      server host name
     * @param port
     *      server port number
     * @param errorHandle
     *      the error handling mechanism
     * @param message
     *      request format message to send
     * @param args
     *      arguments referenced by the format specifiers in the format
     *      string. If there are more arguments than format specifiers, the
     *      extra arguments are ignored. The number of arguments is
     *      variable and may be zero. The maximum number of arguments is
     *      limited by the maximum dimension of a Java array as defined by
     *      <cite>The Java&trade; Virtual Machine Specification</cite>.
     *      The behaviour on a <tt>null</tt> argument depends on the
     *      conversion.
     */
    public static void send(String host, int port, ErrorHandle errorHandle, String message, Object ...args) {
        send(host, port, errorHandle, String.format(message, args));
    }

    /**
     * Send a request to the given host name
     * and port number with an agent created by
     * the factory given. Spawn a thread to handle
     * the request and response. Non-blocking.
     *
     * @param host
     *      server host name
     * @param port
     *      server port number
     * @param clientAgent
     *      the client agent
     */
    public static void send(String host, int port, Agent clientAgent) {
        send(host, port, null, clientAgent);
    }

    /**
     * Send a request to the given host name
     * and port number with given the message.
     * Spawn a thread to handle the request and
     * response. Non-blocking.
     *
     * @param host
     *      server host name
     * @param port
     *      server port number
     * @param message
     *      request message to send
     */
    public static void send(String host, int port, String message) {
        send(host, port, null, message);
    }

    /**
     * Send a request to the given host name
     * and port number with given the message.
     * Spawn a thread to handle the request and
     * response. Non-blocking.
     *
     * @param host
     *      server host name
     * @param port
     *      server port number
     * @param message
     *      request format message to send
     * @param args
     *      arguments referenced by the format specifiers in the format
     *      string. If there are more arguments than format specifiers, the
     *      extra arguments are ignored. The number of arguments is
     *      variable and may be zero. The maximum number of arguments is
     *      limited by the maximum dimension of a Java array as defined by
     *      <cite>The Java&trade; Virtual Machine Specification</cite>.
     *      The behaviour on a <tt>null</tt> argument depends on the
     *      conversion.
     */
    public static void send(String host, int port, String message, Object ...args) {
        send(host, port, null, message, args);
    }

    // Subclasses

    /** Simple error handler implementation. */
    public static class SimpleClientErrorHandle implements ErrorHandle {
        @Override
        public void doError(Throwable e) {
            if (e instanceof UnknownHostException) {
                LOG.error(e, "Unknown host");
            } else if (e instanceof IOException) {
                LOG.error(e, "IO error with socket");
            } else {
                LOG.error(e);
            }
        }
    } // SimpleClientErrorHandle

} // ClientService
