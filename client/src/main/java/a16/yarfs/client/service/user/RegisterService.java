/**
 * Created by jorge at 11/11/17
 **/
package a16.yarfs.client.service.user;

import a16.yarfs.client.ClientConstants;
import a16.yarfs.client.service.exception.AlreadyExecutedException;
import a16.yarfs.client.service.exception.NotExecutedException;
import a16.yarfs.client.service.exception.RegisterServiceException;
import a16.yarfs.client.service.exception.ServiceResultException;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;

/**
 * Class RegisterService
 **/
public class RegisterService extends AbstractUserService {
    private String username;
    private String password;

    public RegisterService(String baseUrl, String username, String password) throws MalformedURLException {
        super(baseUrl, ClientConstants.Endpoints.REGISTER);
        this.username = username;
        this.password = password;
    }

    /**
     * Just perform a register command with the username and password
     *
     * @throws RegisterServiceException in case the request couldn't be processed,
     *                                  should be caught later and eventually presented to the user
     */
    @Override
    public void execute() throws RegisterServiceException {
        JSONObject js = new JSONObject();

        try {
            js.put("username", username);
            js.put("password", password);

            setRequestParameters(js);

            super.execute();

            JSONObject response = getResponse();

            // TODO check if response is ok!


        } catch (ServiceResultException e) {
            // FIXME fine grain exceptions because there can be 2 different cases of ServiceResultException
            getLogger().error("Couldn't do the server register request: " + e.getMessage());
            throw new RegisterServiceException(e.getMessage());

        } catch (NotExecutedException | AlreadyExecutedException e) {
            /* Do nothing, it's fine */
            getLogger().error("This is supposed to never happen. Cosmic rays!");

        } catch (JSONException e) {
            // Error on JSON reads/writes, probably the response is malformed
            getLogger().error("Malformed response from the server: ", e);

        } catch (IOException e) {
            // Worst Case Scenario
            getLogger().error("Something really bad happened: ", e);
        }

    }
}
