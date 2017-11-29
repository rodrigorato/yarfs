/**
 * Created by nuno at 28/11/17
 */
package a16.yarfs.server.handlers;

import a16.yarfs.server.ServerConstants;
import a16.yarfs.server.domain.Manager;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Class UserLogoutHandler
 * Takes care of handling the logout requests.
 */
public final class UserLogoutHandler extends AbstractHttpHandler {
    // FIXME: should it inherit our abstract handler instead?


    public UserLogoutHandler(String ... methods){
        super(methods);
    }

    @Override
    public void handle(HttpExchange httpExchange)  {
        super.handle(httpExchange);
        JSONObject request = null;
        try {
            // Get the request to read the username and password parameters
            request = super.getBodyAsJson(httpExchange);

            logger.debug("Received token " + request.getString("sessid"));
            Manager.getInstance().logout(request.getString("sessid"));

            JSONObject response = new JSONObject();

            super.sendResponse(ServerConstants.ResponseCodes.SUCCESS_CODE, response.toString(), httpExchange);

        } catch (JSONException e) {
            // This happens on a poorly formed request

            super.sendResponse(ServerConstants.ResponseCodes.POORLY_FORMED_REQUEST_CODE,
                    ServerConstants.ResponseCodes.POORLY_FORMED_REQUEST_MESSAGE,
                    httpExchange);

        } catch (Exception e) {
            // Something *bad* happened

            AbstractHttpHandler.logger.error("Something bad happened on login!\n" + e.getMessage()); //FIXME use recently created getInstance
            sendResponse(ServerConstants.ResponseCodes.INTERNAL_SERVER_ERROR_CODE,
                    ServerConstants.ResponseCodes.INTERNAL_SERVER_ERROR_MESSAGE,
                    httpExchange);
        }
    }
}
