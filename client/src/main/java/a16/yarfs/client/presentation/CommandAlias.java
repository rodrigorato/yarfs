/**
 * Created by nuno at 19/11/17
 */
package a16.yarfs.client.presentation;

/**
 *  Class CommandAlias
 *  alias for another command (proxy pattern)
 */
public class CommandAlias extends Command {
    private Command target;

    public CommandAlias(String name, Command target) {
        super(target.getShell(), name, target.getHelp());
        this.target = target;
    }

    @Override
    void execute(String[] args) {
        target.execute(args);
    }

    @Override
    public String getUsage() {
        return target.getUsage();
    }
}
