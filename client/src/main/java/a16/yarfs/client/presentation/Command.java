/**
 * Created by nuno at 19/11/17
 */
package a16.yarfs.client.presentation;

import org.apache.log4j.Logger;

/**
 * Class Command
 * an abstract presentation command
 */
public abstract class Command {
    private static Logger logger = Logger.getLogger(Command.class);

    private Shell sh;
    private String name;
    private String help;

    public Command(Shell sh, String name, String help) {
        this.name = name;
        this.help = help;
        this.sh = sh;
        sh.addCommand(this);
    }

    /**
     * Execute the command, i.e. do the magic.
     * This method should parse the arguments and use them.
     *
     * @param args arguments for the commad
     */
    abstract void execute(String[] args);

    /**
     * get the name of the command that is used to invoked within the Shell
     *
     * @return command name
     */
    public String getName() {
        return name;
    }

    /**
     * get a short string briefly explaining what the command does
     *
     * @return help string
     */
    public String getHelp() {
        return help;
    }

    /**
     * get the shell that owns this command
     *
     * @return the shell
     */
    public Shell getShell() {
        return sh;
    }

    /**
     * get a (multiline) string that explains how the command should be invoked and with which arguments
     *
     * @return usage string
     */
    public String getUsage() {
        return "Usage: " + getName() + " ???";
    }

    protected static Logger getLogger() {
        return Command.logger;
    }
}
