/**
 * Created by nuno at 28/11/17
 */
package a16.yarfs.client.presentation;


import a16.yarfs.client.ClientConstants;
import a16.yarfs.client.service.exception.NotExecutedException;
import a16.yarfs.client.service.exception.ServiceResultException;
import a16.yarfs.client.service.user.LoginService;

import java.io.*;
import java.net.MalformedURLException;

/**
 *  Class LoginCommand
 *  authenticate the user and start a new session, adding it to the shell
 */
public class LoginCommand extends Command {

    /**
     * create a new login command and adds it to the shell
     * @param sh   the YarfsShell where the command will be invoked from
     * @param name the name used to invoke the command
     */
    public LoginCommand(YarfsShell sh, String name) {
        super(sh, name, "start a new session");
    }

    @Override
    void execute(String[] args) {

        // check prerequisites
        YarfsShell shell = (YarfsShell) getShell();
        if (shell.isLoggedIn()) {
            shell.println("You are already logged in!");
            return;
        }

        // check (and parse) arguments
        if (args.length != 2) {
            shell.println(getUsage());
            return;
        }
        String username = args[0];
        String password = args[1];

        try {
            // contact the server
            LoginService service = new LoginService(ClientConstants.baseUrl, username, password);
            service.execute();

            // try to get the result
            String sessionid = service.getSessionId();
            shell.getLogger().info("logged in using token " + sessionid);
            shell.setActiveSessionid(sessionid);
            shell.setActiveUser(username);
        }catch (ServiceResultException e) {
            shell.println("could not login: " + e.getMessage());
        }catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NotExecutedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getUsage() {
        return "Usage: "+ getName() + " <username> <password>";
    }

}
