/**
 * Created by jorge at 11/11/17
 **/
package a16.yarfs.client.service.user;

import a16.yarfs.client.ClientConstants;
import a16.yarfs.client.service.exception.AlreadyExecutedException;
import a16.yarfs.client.service.exception.NotExecutedException;
import a16.yarfs.client.service.exception.ServiceResultException;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;

/**
 Class LoginService
 **/
public class LoginService extends AbstractUserService {
    private String username;
    private String password;

    public LoginService(String baseUrl, String username, String password) throws MalformedURLException {
        super(baseUrl, ClientConstants.Endpoints.LOGIN);
        this.username = username;
        this.password = password;
    }

    @Override
    public void execute() throws IOException, AlreadyExecutedException {
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

    public String getSessionId() throws IOException, ServiceResultException, NotExecutedException {
        assertExecuted();

        try {
            JSONObject js = getResponse();
            return js.getString("sessid");
        } catch (JSONException e) {
            e.printStackTrace();
            throw new ServiceResultException("no sessionid included in the response");
        }
    }
}
