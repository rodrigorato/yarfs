/**
 * Created by jorge at 11/11/17
 **/

package a16.yarfs.client.service.user;

import a16.yarfs.client.ClientConstants;
import a16.yarfs.client.service.exception.AlreadyExecutedException;
import a16.yarfs.client.service.exception.LogoutServiceException;
import a16.yarfs.client.service.exception.NotExecutedException;
import a16.yarfs.client.service.exception.ServiceResultException;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;

/**
 * Class LogoutService
 *       Implements logout functionality for the client.
 */
public class LogoutService extends AbstractUserService{

    private String sessid;
    private static Logger logger = Logger.getLogger(LogoutService.class); //FIXME refactor

    public LogoutService(String baseUrl, String sessid) throws MalformedURLException {
        super(baseUrl, ClientConstants.Endpoints.LOGOUT);
        this.sessid = sessid;
    }

    @Override
    public void execute(){
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("sessid", this.sessid);
            setRequestParameters(requestBody);
            super.execute();
            super.getResponse();
        } catch (ServiceResultException e) {
            throw new LogoutServiceException("Error logging out with session " + sessid);
        } catch (AlreadyExecutedException | NotExecutedException e) {
            logger.error("Something bad really happened while executing my service");
        } catch (JSONException | IOException e) {
            logger.error("Something bad really happened with IO or JSON");
        }

    }

}
