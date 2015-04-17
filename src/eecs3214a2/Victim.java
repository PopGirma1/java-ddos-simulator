package eecs3214a2;

import eecs3214a2.common.*;
import java.io.*;
import java.util.*;
import java.util.logging.*;

/**
 * Victim server in the Distributed
 * Denial Of Service attack
 * demonstration.
 *
 * @author Vincent Chu
 * @version 1.0
 */
public class Victim extends CommandLine implements Closeable {

    /** Server for accepting new requests */
    private final Log L;
    private final ServerService victimServer = new ServerService();

    /**
     * Constructor
     *
     * @param logFile
     *      log file to output logs to
     * @throws IOException
     *      if IO error occurs
     */
    public Victim(File logFile) throws IOException {
        this.L = Log.get("Victim", logFile);
        L.setLevel(Level.INFO);
        add("start", "listen(int)", "port",
                "Starts the server and listens for incoming " +
                "requests at the given 'port'.");
        add("stop", "close", "",
                "Stops the server.");
    }

    /**
     * Listens on the given port
     * to handle new incoming requests.
     * Spawns a new thread to accept
     * new requests.
     *
     * @param port
     *      TCP socket port to listen to
     * @throws IOException
     *      cannot acquire server socket
     */
    public void listen(Integer port) throws IOException {
        if (!victimServer.isClosed()) {
            throw new IllegalStateException("Server already listening");
        }
        victimServer.listen(EchoAgent.class, port, this);
    }

    @Override
    public void close() throws IOException {
        victimServer.close();
    }

    // Subclasses

    /**
     * Agent that implements the handling
     * the server behaviour. Echos the received
     * messages and logs the messages. Stops
     * connection when receives ETX control
     * character.
     */
    public class EchoAgent implements Agent {
        @Override
        public boolean doAction(Map<String, Object> ctx,
                                String msg, PrintWriter out) throws IOException {
            if (msg == null) {return true;}
            if ("\u0003".equals(msg)) {return false;}
            out.println(L.info(msg));
            return true;
        }
    } // EchoAgent

    // For Testing Purposes Only

    public static void main(String ...args) throws IOException {
        (new Victim(new File(args[0]))).run();
    }

} // Victim
