package a16.yarfs.client.presentation;

public class ListUsersCommand extends Command{
    public ListUsersCommand(Shell sh, String name, String help) {
        super(sh, name, help);
    }

    @Override
    void execute(String[] args) {
        YarfsShell shell = (YarfsShell) getShell();

    }
}
