/**
 * Created by jorge at 11/11/17
 **/
package a16.yarfs.client.command.user;

import a16.yarfs.client.ClientConstants;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import java.net.MalformedURLException;

/**
 Class LoginCommand

 **/
public class LoginCommand extends AbstractUserCommand {
    private String username;
    private String password;

    public LoginCommand(String baseUrl, String username, String password) throws MalformedURLException {
        super(baseUrl, ClientConstants.Endpoints.LOGIN);
        this.username = username;
        this.password = password;
    }
    }
}
