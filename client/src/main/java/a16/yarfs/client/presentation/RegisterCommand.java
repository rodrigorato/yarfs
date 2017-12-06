/**
 * Created by nuno at 28/11/17
 */
package a16.yarfs.client.presentation;


import a16.yarfs.client.ClientConstants;
import a16.yarfs.client.service.exception.AlreadyExecutedException;
import a16.yarfs.client.service.exception.RegisterServiceException;
import a16.yarfs.client.service.exception.ServiceExecutionException;
import a16.yarfs.client.service.user.RegisterService;

import java.net.MalformedURLException;

/**
 * Class RegisterCommand
 * Request to register a username and password to the server
 */
public class RegisterCommand extends Command {

    /**
     * create a new register command and adds it to the shell
     *
     * @param sh   the YarfsShell where the command will be invoked from
     * @param name the name used to invoke this command
     */
    public RegisterCommand(YarfsShell sh, String name) {
        super(sh, name, "register a new user and password");
    }

    @Override
    void execute(String[] args) {

        // Only register when not logged in
        YarfsShell shell = (YarfsShell) getShell();
        if (shell.isLoggedIn()) {
            shell.println("You are already logged in! Cannot register a new user now.");
            return;
        }

        // check (and parse) arguments - username and password
        if (args.length != 2) {
            shell.println(getUsage());
            return;
        }
        String username = args[0];
        String password = args[1];

        try {

            // contact the server via the RegisterService
            RegisterService service = new RegisterService(ClientConstants.baseServerUrl, username, password);
            service.execute();


        } catch (RegisterServiceException e) {
            shell.println("could not register: " + e.getMessage());
        } catch (MalformedURLException e) {
            // This should never happen but oh well...
            getLogger().error("Malformed URL on the RegisterCommand", e);
        } catch (AlreadyExecutedException e) {
            e.printStackTrace(); // should never happen, the service was just instantiated
        } catch (ServiceExecutionException e) {
            shell.println("error: " + e.getMessage());
        }
    }

    @Override
    public String getUsage() {
        return "Usage: " + getName() + " <username> <password>";
    }

}
