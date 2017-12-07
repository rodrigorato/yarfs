/**
 * Created by jorge at 11/11/17
 **/
package a16.yarfs.ca.service;

import a16.yarfs.ca.CAConstants;
import a16.yarfs.ca.service.exception.AlreadyExecutedException;
import a16.yarfs.ca.service.exception.NotExecutedException;
import a16.yarfs.ca.service.exception.ServiceExecutionException;
import a16.yarfs.ca.service.exception.ServiceResultException;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;

/**
 Class AuthenticateService
 **/
public class AuthenticateService extends AbstractHttpService {
    private String sessionid;
    private String username;

    public AuthenticateService(String baseUrl, String sessionid, String username) throws MalformedURLException {
        super(baseUrl, CAConstants.Endpoints.AUTHENTICATE);
        this.sessionid = sessionid;
        this.username = username;
    }

    @Override
    public void execute() throws IOException, AlreadyExecutedException, ServiceExecutionException {
        JSONObject js = new JSONObject();
        try {
            js.put("sessionid", sessionid);
            js.put("username", username);
            setRequestParameters(js);
            super.execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public boolean isAuthenticated() throws IOException, ServiceResultException, NotExecutedException {
        assertExecuted();

        try {
            JSONObject js = getResponse();

            getLogger().debug("Trying to authenticate user "+username+" with session "+sessionid);

            String requestUsername = js.getString("username");
            String requestSessionId = js.getString("sessionid");

            boolean requestAuthenticated = js.getBoolean("authenticated");
            getLogger().debug("Request authen is "+requestAuthenticated);

            return username.equals(requestUsername) &&
                   sessionid.equals(requestSessionId) &&
                   requestAuthenticated;

        } catch (JSONException e) {
            e.printStackTrace();
            throw new ServiceResultException("no sessionid included in the response");
        }
    }

}
