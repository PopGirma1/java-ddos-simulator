import eecs3214a2.*;
import java.io.*;

public class Main {
    public static void main(String[] args) {
        Runnable runnable = null;
        try {
            switch (args[0]) {
                case "CmdCtrl":
                    runnable = new CmdCtrl();
                    break;
                case "Attacker":
                    runnable = new Attacker(args[1], Integer.parseInt(args[2]));
                    break;
                case "Victim":
                    runnable = new Victim(new File(args[1]));
                    break;
                default:
                    throw new RuntimeException("Unknown mode");
            }
        } catch (Exception e) {
            if (e instanceof ArrayIndexOutOfBoundsException) {
                System.err.println("Missing arguments");
            } else {
                System.err.println(e.getMessage());
            }
            System.err.println("Usage: java Main CmdCtrl");
            System.err.println("       java Main Attacker <cchost> <ccport>");
            System.err.println("       java Main Victim   <logfile>");
            System.exit(-1);
        }
        runnable.run();
    }
} // Main
