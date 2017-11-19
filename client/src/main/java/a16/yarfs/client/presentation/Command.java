/**
 * Created by nuno at 19/11/17
 */
package a16.yarfs.client.presentation;

/**
 *  Class Command
 *  an abstract presentation command
 */
public abstract class Command {
    private Shell sh;
    private String name;
    private String help;

    public Command(Shell sh, String name) {
        this(sh, name, "<no help>");
    }

    public Command(Shell sh, String name, String help) {
        this.name = name;
        this.help = help;
        this.sh = sh;
        sh.addCommand(this);
    }

    abstract void execute(String[] args);

    public String getName() {
        return name;
    }

    public String getHelp() {
        return help;
    }

    public Shell getShell() {
        return sh;
    }
}
