package eecs3214a2.common;

import java.lang.reflect.*;
import java.util.*;
import java.util.regex.*;

public class CommandExecutor {

    protected final Log log = Log.get();

    /**
     * Map containing the valid commands that is
     * supported by this Command Line interface.
     *
     * cmdName, methodName(arguments)
     */
    protected final HashMap<String, String> validCommands = new HashMap<>();

    /**
     * Adds the given command name and method name with
     * argument types to the executor's list of recognized
     * commands.
     *
     * @param cmdName
     *      the command name to used
     * @param methodName
     *      the method name and argument type list in
     *      parentheses delimited by comma
     * @return
     *      this executor object
     */
    public CommandExecutor add(String cmdName, String methodName) {
        validCommands.put(cmdName.toUpperCase(), methodName);
        return this;
    }

    /**
     * Executes the given command via input
     * in the context of the given object.
     *
     * @param context
     *      object to invoke command on
     * @param input
     *      the input command string
     */
    public void execute(Object context, String input) {
        try {
            // Valid input?
            if (input.trim().matches("\\S+(\\s+(\\S+|\"[^\"]*\"))*")) {
                String[] tk = input.trim().split("\\s+", 2);
                String parsedCommand = tk[0].toUpperCase();
                String parsedArgument = tk.length == 2 ? tk[1] : "";
                String[] arguments = parsedArgument.isEmpty() ? new String[0] : parsedArgument.split("\\s+");
                String argumentList = arguments.length > 0 ? String.join(", ", arguments) : "";
                // Get any argument supplied with the command
                log.verb("Parsed command : %s Arguments: %s", parsedCommand, argumentList);
                // Valid command?
                if (validCommands.containsKey(parsedCommand)) {
                    // Parse method name and arguments
                    String command = validCommands.get(parsedCommand);
                    Pattern p = Pattern.compile("(?<command>[A-Za-z_]+)(\\((?<argument>.*)\\))?");
                    Matcher m = p.matcher(command);
                    // Is valid method name and arguments?
                    log.verb("Calling method : %s", command);
                    if (m.matches()) {
                        String method = m.group("command");
                        // Get any argument supplied with the command
                        String parsedTypes = m.group("argument") != null ? m.group("argument").trim() : "";
                        String[] types = parsedTypes.isEmpty() ? new String[0] : parsedTypes.split("\\s*,\\s*");
                        // Valid number of arguments?
                        if (arguments.length == types.length) {
                            Object[] args = new Object[types.length];
                            Class[] classes = new Class[types.length];
                            // Parse arguments as specified by the types
                            try {
                                for (int i = 0; i < types.length; i++) {
                                    switch (types[i]) {
                                        case "boolean": args[i] = Boolean.parseBoolean(arguments[i]); break;
                                        case "char":    args[i] = arguments[i].charAt(0);             break;
                                        case "int":     args[i] = Integer.parseInt(arguments[i]);     break;
                                        case "long":    args[i] = Long.parseLong(arguments[i]);       break;
                                        case "float":   args[i] = Float.parseFloat(arguments[i]);     break;
                                        case "double":  args[i] = Double.parseDouble(arguments[i]);   break;
                                        default:        args[i] = arguments[i];
                                    }
                                }
                            } catch (Exception e) {
                                throwArgumentParseError(command, e);
                            }
                            // Populate classes
                            for (int i = 0; i < args.length; i++) {
                                classes[i] = args[i].getClass();
                            }
                            // Execute the command on the class using Reflections.
                            context.getClass().getMethod(method, classes).invoke(context, args);
                        } else {
                            throwArgumentMismatchError(command, types.length);
                        }
                    } else {
                        throwSyntaxError(command);
                    }
                } else {
                    throwSyntaxError(parsedCommand + " " + parsedArgument);
                }
            } else if (input.length() > 0) {
                throwUnrecognizedCmd(input);
            }
        } catch (IllegalAccessException
                | IllegalArgumentException
                | NoSuchMethodException
                | SecurityException e) {
            log.error(e);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            log.error((cause != null) ? cause : e);
        }
    }

    // Exceptions

    private void throwArgumentParseError(String command, Throwable e) {
        throw new IllegalArgumentException("Argument parse error. '" + command + "'.", e);
    }
    private void throwArgumentMismatchError(String command, int argc) {
        throw new IllegalArgumentException("Arguments mismatched. '" + command + "'. Expected " + argc + ".");
    }
    private void throwUnrecognizedCmd(String command) throws NoSuchMethodException {
        throw new NoSuchMethodException("Unrecognized command. '" + command + "'.");
    }
    private void throwSyntaxError(String command) throws NoSuchMethodException {
        throw new NoSuchMethodException("Syntax error. '" + command + "'.");
    }

} // CommandExecutor
