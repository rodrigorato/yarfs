/**
 * Created by jorge at 11/11/17
 **/
package a16.yarfs.client.command.user;

import a16.yarfs.client.ClientConstants;
import a16.yarfs.client.command.exception.CommandResultException;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
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

    @Override
    public void execute() throws IOException {
        JSONObject js = new JSONObject();
        try {
            js.put("username", username);
            js.put("password", password);
            setRequestParameters(js);
            super.execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getSessionId() throws IOException {
        assertExecuted();

        try {
            JSONObject js = getResponse();
            return js.getString("sessid");
        } catch (JSONException e) {
            throw new CommandResultException("invalid server response");
        }
    }
}
