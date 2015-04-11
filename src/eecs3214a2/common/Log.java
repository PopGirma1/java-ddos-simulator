package eecs3214a2.common;

import java.text.*;
import java.util.*;
import java.util.logging.*;
import java.io.*;

/**
 * A convenience handler for the
 * print or logging error messages.
 *
 * @author Vincent Chu
 * @version 1.0
 */
public class Log {

    /**
     * Name for the logger
     * Level of logger output
     * Writer for logger to output to
     */
    private final String name;
    private final List<PrintWriter> writers = new ArrayList<>();
    private Level level;

    /**
     * Constructs a logger handler.
     *
     * @param name
     *      a name for the logger
     */
    private Log(String name) {
        this.name = name;
        setLevel(Level.INFO);
        addWriter(new PrintWriter(System.out, true));
    }

    /**
     * Set the logging level
     *
     * @param level
     *      logging level
     */
    public void setLevel(Level level) {
        this.level = level;
    }

    /**
     * Set the output print writer
     *
     * @param writer
     *      output print writer
     */
    public void addWriter(PrintWriter writer) {
        synchronized (writers) {
            writers.add(writer);
        }
    }

    /**
     * Remove all the output print writer.
     */
    public void clearWriters() {
        synchronized (writers) {
            for (PrintWriter writer : writers) {
                writer.close();
            }
            writers.clear();
        }
    }

    // Logging

    /**
     * Return the formatted message.
     *
     * @param logLevel
     *      the logging verbosity level
     * @param message
     *      the message or format string
     * @return
     *      formatted message
     */
    private String formatMessage(Level logLevel, String message) {
        final String format = "yyyy-MM-dd'T'HH:mm:ssz";
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        String dateTime = dateFormat.format(new Date());
        return (name == null ? "" : name + " ") + String.format(
                "%s [%s]: %s", dateTime, logLevel.getName(), message);
    }

    /**
     * Logs a message and return the formatted message.
     *
     * @param logLevel
     *      the logging verbosity level
     * @param message
     *      the message or format string
     * @param args
     *      arguments referenced by the format specifiers in the format
     *      string. If there are more arguments than format specifiers, the
     *      extra arguments are ignored. The number of arguments is
     *      variable and may be zero. The maximum number of arguments is
     *      limited by the maximum dimension of a Java array as defined by
     *      <cite>The Java&trade; Virtual Machine Specification</cite>.
     *      The behaviour on a <tt>null</tt> argument depends on the
     *      conversion.
     * @return
     *      the formatted message
     */
    public String log(Level logLevel, String message, Object... args) {
        if (args.length > 0) {
            message = String.format(message, args);
        }
        if (this.level.intValue() <= logLevel.intValue()) {
            synchronized (writers) {
                for (PrintWriter writer : writers) {
                    writer.println(formatMessage(logLevel, message));
                }
            }
        }
        return message;
    }

    /**
     * Logs a verbose (finest) message and
     * return the formatted message. Shorthand
     * for log(Level.FINEST, message, args).
     *
     * @param message
     *      the message or format string
     * @param args
     *      arguments referenced by the format specifiers in the format
     *      string. If there are more arguments than format specifiers, the
     *      extra arguments are ignored. The number of arguments is
     *      variable and may be zero. The maximum number of arguments is
     *      limited by the maximum dimension of a Java array as defined by
     *      <cite>The Java&trade; Virtual Machine Specification</cite>.
     *      The behaviour on a <tt>null</tt> argument depends on the
     *      conversion.
     * @return
     *      the formatted message
     */
    public String vvv(String message, Object... args) {
        return log(Level.FINEST, message, args);
    }

    /**
     * Logs a verbose (finer) message and
     * return the formatted message. Shorthand
     * for log(Level.FINER, message, args).
     *
     * @param message
     *      the message or format string
     * @param args
     *      arguments referenced by the format specifiers in the format
     *      string. If there are more arguments than format specifiers, the
     *      extra arguments are ignored. The number of arguments is
     *      variable and may be zero. The maximum number of arguments is
     *      limited by the maximum dimension of a Java array as defined by
     *      <cite>The Java&trade; Virtual Machine Specification</cite>.
     *      The behaviour on a <tt>null</tt> argument depends on the
     *      conversion.
     * @return
     *      the formatted message
     */
    public String vv(String message, Object... args) {
        return log(Level.FINER, message, args);
    }

    /**
     * Logs a verbose (fine) message and
     * return the formatted message. Shorthand
     * for log(Level.FINE, message, args).
     *
     * @param message
     *      the message or format string
     * @param args
     *      arguments referenced by the format specifiers in the format
     *      string. If there are more arguments than format specifiers, the
     *      extra arguments are ignored. The number of arguments is
     *      variable and may be zero. The maximum number of arguments is
     *      limited by the maximum dimension of a Java array as defined by
     *      <cite>The Java&trade; Virtual Machine Specification</cite>.
     *      The behaviour on a <tt>null</tt> argument depends on the
     *      conversion.
     * @return
     *      the formatted message
     */
    public String verb(String message, Object... args) {
        return log(Level.FINE, message, args);
    }

    /**
     * Logs a configuration message and return the formatted message.
     * Shorthand for log(Level.CONFIG, message, args).
     *
     * @param message
     *      the message or format string
     * @param args
     *      arguments referenced by the format specifiers in the format
     *      string. If there are more arguments than format specifiers, the
     *      extra arguments are ignored. The number of arguments is
     *      variable and may be zero. The maximum number of arguments is
     *      limited by the maximum dimension of a Java array as defined by
     *      <cite>The Java&trade; Virtual Machine Specification</cite>.
     *      The behaviour on a <tt>null</tt> argument depends on the
     *      conversion.
     * @return
     *      the formatted message
     */
    public String conf(String message, Object... args) {
        return log(Level.CONFIG, message, args);
    }

    /**
     * Logs an information message and return the formatted message.
     * Shorthand for log(Level.INFO, message, args).
     *
     * @param message
     *      the message or format string
     * @param args
     *      arguments referenced by the format specifiers in the format
     *      string. If there are more arguments than format specifiers, the
     *      extra arguments are ignored. The number of arguments is
     *      variable and may be zero. The maximum number of arguments is
     *      limited by the maximum dimension of a Java array as defined by
     *      <cite>The Java&trade; Virtual Machine Specification</cite>.
     *      The behaviour on a <tt>null</tt> argument depends on the
     *      conversion.
     * @return
     *      the formatted message
     */
    public String info(String message, Object... args) {
        return log(Level.INFO, message, args);
    }

    /**
     * Logs a warning message and return the formatted message.
     * Shorthand for log(Level.WARNING, message, args).
     *
     * @param message
     *      the message or format string
     * @param args
     *      arguments referenced by the format specifiers in the format
     *      string. If there are more arguments than format specifiers, the
     *      extra arguments are ignored. The number of arguments is
     *      variable and may be zero. The maximum number of arguments is
     *      limited by the maximum dimension of a Java array as defined by
     *      <cite>The Java&trade; Virtual Machine Specification</cite>.
     *      The behaviour on a <tt>null</tt> argument depends on the
     *      conversion.
     * @return
     *      the formatted message
     */
    public String warn(String message, Object... args) {
        return log(Level.WARNING, message, args);
    }

    /**
     * Logs an error message and return the formatted message.
     * Shorthand for log(Level.SEVERE, message, args).
     *
     * @param message
     *      the message or format string
     * @param args
     *      arguments referenced by the format specifiers in the format
     *      string. If there are more arguments than format specifiers, the
     *      extra arguments are ignored. The number of arguments is
     *      variable and may be zero. The maximum number of arguments is
     *      limited by the maximum dimension of a Java array as defined by
     *      <cite>The Java&trade; Virtual Machine Specification</cite>.
     *      The behaviour on a <tt>null</tt> argument depends on the
     *      conversion.
     * @return
     *      the formatted message
     */
    public String error(String message, Object... args) {
        return log(Level.SEVERE, message, args);
    }

    /**
     * Logs an error message given a message and a throwable object.
     * Returns the formatted message.
     *
     * @param e
     *      error object
     * @param message
     *      the message or format string
     * @param args
     *      arguments referenced by the format specifiers in the format
     *      string. If there are more arguments than format specifiers, the
     *      extra arguments are ignored. The number of arguments is
     *      variable and may be zero. The maximum number of arguments is
     *      limited by the maximum dimension of a Java array as defined by
     *      <cite>The Java&trade; Virtual Machine Specification</cite>.
     *      The behaviour on a <tt>null</tt> argument depends on the
     *      conversion.
     * @return
     *      the formatted message
     */
    public String error(Throwable e, String message, Object... args) {
        return error(message, args) + verb(errorMsg(e));
    }

    /**
     * Logs an error message given a throwable object.
     * Returns the formatted message.
     *
     * @param e
     *      error object
     * @return
     *      the formatted message
     */
    public String error(Throwable e) {
        return error(e.getMessage()) + verb(errorMsg(e));
    }

    /**
     * Given a throwable object
     * returns the formatted message.
     *
     * @param e
     *      error object
     * @return
     *      the formatted message
     */
    public static String errorMsg(Throwable e) {
        StringBuilder sb = new StringBuilder();
        while (e != null) {
            sb.append(e.getMessage());
            sb.append("\n");
            for (StackTraceElement ste : e.getStackTrace()) {
                sb.append("\t");
                sb.append(ste.toString());
                sb.append("\n");
            }
            e = e.getCause();
        }
        return sb.toString();
    }

    // Static

    private static final Map<String, Log> logs = new HashMap<>();

    /**
     * Returns the log with the given name.
     * If no log with the name is not found
     * creates a new log.
     *
     * @param name
     *      name of the logger
     * @return
     *      existing or newly created logger
     */
    public static Log get(String name) {
        if (!logs.containsKey(name)) {
            logs.put(name, new Log(name));
        }
        return logs.get(name);
    }

    /**
     * Returns the shared global log.
     *
     * @return
     *      global log
     */
    public static Log get() {
        return Log.get(null);
    }

    /**
     * Returns a logger for the given name
     * and given log file to output to.
     *
     * @param name
     *      name of the logger
     * @param logFile
     *      file to output logs to
     * @return
     *      the logger
     * @throws IOException
     *      if IO error occurs
     */
    public static Log get(String name, File logFile) throws IOException {
        logFile.createNewFile();
        PrintWriter writer = new PrintWriter(new FileOutputStream(logFile, true), true);
        Log log = get(name);
        log.addWriter(writer);
        return log;
    }

} // Logger
