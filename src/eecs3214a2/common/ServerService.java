package eecs3214a2.common;

import java.lang.reflect.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * Generic implementation of a
 * server service with thread
 * management and request
 * handling.
 *
 * @author Vincent Chu
 * @version 1.0
 */
public class ServerService extends Thread implements Closeable {

    private static final Log LOG = Log.get();

    private ServerSocket serverSocket;
    private AgentFactory agentFactory;

    /**
     * Listens on the given port and
     * uses the given factory to create
     * agents to handle new incoming
     * requests. Spawns a new thread to
     * accept new requests.
     *
     * @param factory
     *      for creating new agent objects
     * @param port
     *      TCP socket port to listen to
     * @throws IOException
     *      cannot acquire server socket
     */
    public void listen(AgentFactory factory, int port) throws IOException {
        if (!isClosed()) {
            throw new AssertionError("Server socket opened already.");
        }
        agentFactory = factory;
        serverSocket = new ServerSocket(port);
        LOG.verb("Listening port %d", port);
        start();
    }

    /**
     * Listens on the given port and
     * uses the given agent class to
     * create agents to handle new incoming
     * requests. Spawns a new thread to
     * accept new requests.
     *
     * @param agentClass
     *      agent class
     * @param port
     *      TCP socket port to listen to
     * @param context
     *      object that encloses agentClass,
     *      maybe null if agentClass is static
     * @throws IOException
     *      cannot acquire server socket
     */
    public void listen(Class<? extends Agent> agentClass, int port, Object context) throws IOException {
        listen(new AgentFactory() {
            @Override
            public Agent createAgent(Map<String, Object> ctx) {
                try {
                    if (context == null) {
                        return agentClass.newInstance();
                    } else {
                        Constructor<? extends Agent> construct =
                                agentClass.getDeclaredConstructor(context.getClass());
                        return construct.newInstance(context);
                    }
                } catch (Exception e) {
                    LOG.error(e);
                    e.printStackTrace();
                    return null;
                }
            }
        }, port);
    }

    /**
     * Listens on the given port and
     * uses the given agent class to
     * create agents to handle new incoming
     * requests. Spawns a new thread to
     * accept new requests.
     *
     * @param agentClass
     *      agent class
     * @param port
     *      TCP socket port to listen to
     * @throws IOException
     *      cannot acquire server socket
     */
    public void listen(Class<? extends Agent> agentClass, int port) throws IOException {
        listen(agentClass, port, null);
    }

    /**
     * Determines if the server socket is
     * closed or not.
     *
     * @return
     *      true if the server socket is
     *      closed false otherwise.
     */
    public boolean isClosed() {
        return serverSocket == null || serverSocket.isClosed();
    }

    @Override
    public void close() throws IOException {
        if (!isClosed()) {
            serverSocket.close();
            LOG.verb("Server socket closed");
            serverSocket = null;
        }
    }

    @Override
    public void run() {
        ExecutorService es = Executors.newCachedThreadPool();
        while (!isClosed()) {
            try {
                final Socket clientSocket = serverSocket.accept();

                // Spawn a new thread from the cached
                // thread pool for each newly arriving
                // connection.
                es.execute(new Runnable() {
                    @Override
                    public void run() {
                        try (Socket socket = clientSocket;
                             InputStream in = socket.getInputStream();
                             OutputStream out = socket.getOutputStream();
                             PrintWriter writer = new PrintWriter(out, true);
                             BufferedReader reader = new BufferedReader(new InputStreamReader(in))
                        ) {
                            Map<String, Object> context = new HashMap<>();

                            // Populated connection context
                            InetAddress host   = serverSocket.getInetAddress();
                            InetAddress remote = socket.getInetAddress();
                            context.put("SERVER_ADDR", host.getHostAddress());
                            context.put("SERVER_NAME", host.getHostName());
                            context.put("SERVER_PORT", serverSocket.getLocalPort());
                            context.put("REMOTE_ADDR", remote.getHostAddress());
                            context.put("REMOTE_HOST", remote.getHostName());
                            context.put("REMOTE_PORT", socket.getPort());
                            context.put("LOCAL_PORT" , socket.getLocalPort());

                            Agent serverAgent = agentFactory.createAgent(context);
                            if (serverAgent.doAction(context, null, writer)) {
                                for (;;) {
                                    String message = reader.readLine();
                                    LOG.info("from client: " + message);
                                    if (message == null) {
                                        break;
                                    } else if (!serverAgent.doAction(context, message, writer)) {
                                        break;
                                    }
                                }
                            }
                            LOG.verb("connection closed");
                        } catch (IOException e) {
                            LOG.error(e, "IO error with socket");
                        } catch (Exception e) {
                            LOG.error(e);
                        }
                    }
                });
            } catch (IOException e) {
                if (!isClosed()) {
                    LOG.error(e, "Cannot accept connection");
                }
            } catch (Exception e) {
                LOG.error(e);
            }
        }
    }

} // ServerService
