package eecs3214a2.common;

import java.util.*;

public class CommandLine extends CommandExecutor implements Runnable {

    /** Input source */
    protected final Scanner in = new Scanner(System.in);

    public CommandLine() {
        add("exit", "exit");
        add("help", "printHelp");
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
        System.out.println("EXIT: Quit the program.");
        System.out.println("HELP: Print this usage output.");
    }

    /**
     * Exits the system.
     */
    public void exit() {
        System.exit(0);
    }

} // CommandLine
