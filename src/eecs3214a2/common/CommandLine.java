package eecs3214a2.common;

import java.util.*;

public class CommandLine extends CommandExecutor implements Runnable {

    /** Input source */
    protected final Scanner in = new Scanner(System.in);

    /**
     * Map containing the help arguments and usage information
     * that is supported by this Command Line interface.
     */
    protected final HashMap<String, String[]> helpArguments = new HashMap<>();
    protected final HashMap<String, String> helpUsages = new HashMap<>();

    public CommandLine() {
        add("exit", "exit", "", "Terminates the program.");
        add("help", "printHelp", "", "Print this usage output.");
    }

    /**
     * Adds the given command name, method name with
     * argument types to the executor's list of recognized
     * commands, the argument names, and the usage information.
     *
     * @param cmdName
     *      the command name to used
     * @param methodName
     *      the method name and argument type list in
     *      parentheses delimited by comma
     * @param arguments
     *      the argument names, delimited by whitespaces
     * @param usage
     *      the usage information
     * @return
     *      this executor object
     */
    public CommandLine add(String cmdName, String methodName,
                           String arguments, String usage) {
        cmdName = cmdName.toUpperCase();
        add(cmdName, methodName);
        helpArguments.put(cmdName, arguments.split("\\s+"));
        helpUsages.put(cmdName, usage);
        return this;
    }

    /**
     * Accepts user input, validates it's syntax and executes the
     * corresponding method associated with it as specified
     * by the validCommands HashMap. This method is encapsulated
     * in an infinite loop to accept user input till program termination.
     */
    @Override
    public void run() {
        prompt();
        while (in.hasNextLine()) {
            // Get input
            execute(this, in.nextLine());
            prompt();
        }
    }

    /**
     * Print a prompt text to indicate
     * to the user to enter input.
     */
    protected void prompt() {
        System.out.print(">>> ");
    }

    /**
     * Print the help and usage message.
     */
    public void printHelp() {
        final String indent = " " + StringUtils.join(" ", new String[4]);
        System.out.println();
        for (String cmdName : validCommands.keySet()) {
            cmdName = cmdName.toUpperCase();
            String[] arguments = helpArguments.get(cmdName);
            System.out.print(indent + cmdName);
            if (arguments != null && arguments.length > 0) {
                System.out.print(" " + StringUtils.join(" ", arguments));
            }
            System.out.println();
            printUsage(helpUsages.get(cmdName));
            System.out.println();
        }
    }

    private void printUsage(String usage) {
        final String indent = " " + StringUtils.join(" ", new String[8]);
        final StringBuilder sb = new StringBuilder();
        final int width = 60;
        for (String word : usage.split("\\s+")) {
            if (sb.length() == 0) {
                sb.append(word);
            } else if (sb.length() + word.length() + 1 > width) {
                System.out.println(indent + sb.toString());
                sb.delete(0, sb.length()); // clear
                sb.append(word);
            } else {
                sb.append(' ');
                sb.append(word);
            }
        }
        System.out.println(indent + sb.toString());
    }

    /**
     * Exits the system.
     */
    public void exit() {
        System.exit(0);
    }

    protected void throwArgumentParseError(String command, Throwable e) {
        printError("Argument parse error. '" + command + "'.");
    }
    protected void throwArgumentMismatchError(String command, int argc) {
        printError("Arguments mismatched. '" + command + "'. Expected " + argc + ".");
    }
    protected void throwUnrecognizedCmd(String command) throws NoSuchMethodException {
        printError("Unrecognized command. '" + command + "'.");
    }
    protected void throwSyntaxError(String command) throws NoSuchMethodException {
        printError("Syntax error. '" + command + "'.");
    }

    private void printError(String message) {
        System.out.println(message);
        printHelp();
    }

} // CommandLine
