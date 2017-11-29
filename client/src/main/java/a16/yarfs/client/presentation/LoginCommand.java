/**
 * Created by nuno at 28/11/17
 */
package a16.yarfs.client.presentation;


import a16.yarfs.client.ClientConstants;
import a16.yarfs.client.service.user.LoginService;

import java.io.*;
import java.net.MalformedURLException;

/**
 *  Class LoginCommand
 *  nuno is an IDIOT because it hasn't made documentation for this class.
 */
public class LoginCommand extends Command {

    private LoginService command;


    public LoginCommand(YarfsShell sh, String name) {
        super(sh, name, "start a new session");
    }

    @Override
    void execute(String[] args) {
        YarfsShell shell = (YarfsShell)getShell();
        if(shell.isLoggedIn()) {
            shell.println("You are already logged in!");
            return;
        }

        if(args.length != 2) {
            shell.println(getUsage());
            return;
        }

        String username = args[0];
        String password = args[1];

        try {
            command = new LoginService(ClientConstants.baseUrl, username, password);
            command.execute();
            String sessionid = command.getSessionId();
            shell.getLogger().info("logged in using token " + sessionid);
            shell.setActiveSessionid(sessionid);
            shell.setActiveUser(username);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getUsage() {
        return "Usage: "+ getName() + " <username> <password>";
    }

}
