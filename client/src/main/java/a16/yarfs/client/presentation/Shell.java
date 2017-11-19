/**
 * Created by nuno at 19/11/17
 */
package a16.yarfs.client.presentation;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 *  Class Shell
 *  reads and executes presentation commands from a given Stream
 */
public abstract class Shell {
    private static Logger logger = Logger.getLogger(Shell.class);
    private String name;
    private PrintWriter out;
    private InputStream in;
    /** commands that this shell accepts */
    private Map<String, Command> cmds = new TreeMap<>();

    private String prompt;

    /**
     * called when the "quit" command is invoked
     */
    protected void onQuitCommand() {
        System.exit(0);
    }

    protected void printPrompt() {
        print(prompt);
        out.flush();
    }

    public Shell(String name, InputStream is, PrintStream w, boolean flush) {
        this.name = name;
        in = is;
        out = new PrintWriter(w, flush);
        prompt = name + " $ ";

        Command quit = new Command(this, "quit", "quit the application") {
            @Override
            void execute(String[] args) {
                onQuitCommand();
            }
        };

        new CommandAlias("exit", quit);

        new Command(this, "help", "list available commands or command help") {
            @Override
            void execute(String[] args) {
                if(args.length == 0) {
                    println(getShell().getName() + " available commands:");
                    println("");
                    List<String> availableCmds = availableCommands();
                    Collections.sort(availableCmds);
                    for(String cmdName : availableCmds) {
                        Command c = getShell().getCommand(cmdName);
                        println("  " + whatIs(c));
                    }
                } else {
                    String cmdName = args[0];
                    Command c = getShell().getCommand(cmdName);
                    println(whatIs(c));
                }
            }
        };
    }

    protected String whatIs(Command cmd) {
        return cmd.getName() + " -\t" + cmd.getHelp();
    }

    private void println(String s) {
        out.println(s);
    }

    private void print(String s) {
        out.print(s);
    }

    /** constructs a list of the commands this Shell accepts */
    protected List<String> availableCommands() {
        Collection<String> av = Collections.unmodifiableCollection(cmds.keySet());
        return new ArrayList<>(av);
    }


    /** add a command to be available on this shell
     *
     * @param c the command
     * @returns false if the command redefined an existing command
     */
    boolean addCommand(Command c) {
        Command prev = cmds.put(c.getName(), c);
        return prev != null;
    }

    /** get an existing command from this Shell
     * @param name the command name
     * @returns null if no command exists with the given name
     */
    public Command getCommand(String name) {
        return cmds.get(name);
    }

    /**
     * Start this shell's read-eval-execute loop, reading and executing commands
     * @throws IOException
     */
    public void run() throws IOException {
        BufferedReader input = new BufferedReader(new InputStreamReader(in));
        printPrompt();
        String line;
        while((line = input.readLine()) != null) {
            String args[] = line.split(" ");
            String cmdName = args[0];
            Command c = getCommand(cmdName);
            if(c == null) {
                if(!cmdName.isEmpty()) {
                    println(getName() + ": " + cmdName + ": command not found");
                }
            } else {
                try {
                    c.execute(Arrays.copyOfRange(args, 1, args.length));
                } catch (RuntimeException e) {
                    System.err.print(cmdName + " error: " + e);
                    e.printStackTrace();
                }
            }
            printPrompt();
        }
        /*
        if(line == null) {
            onQuitCommand();
        }
        */
        println("EOF... bye!");
    }

    public String getName() {
        return name;
    }
}
