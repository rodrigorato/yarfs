/**
 * Created by nuno at 28/11/17
 */
package a16.yarfs.server.handlers;

import a16.yarfs.server.ServerConstants;
import a16.yarfs.server.domain.Manager;
import a16.yarfs.server.exception.api.LoginException;
import a16.yarfs.server.exception.http.InternalServerErrorException;
import com.sun.net.httpserver.HttpExchange;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class UserLoginHandler
 * <p>
 * Used when a login command
 */
public final class UserLoginHandler extends AbstractHttpHandler {

    public UserLoginHandler(String... methods) {
        super(methods);
    }

    @Override
    public void handle(HttpExchange httpExchange) {
        super.handle(httpExchange);

        JSONObject request = null;
        try {
            // Get the request to read the username and password parameters
            request = super.getBodyAsJson(httpExchange);

            // Use those parameters to get (or not) a session id from the Manager
            String sessid = Manager.getInstance() // If no such user/password then LoginException is thrown
                    .loginUser(request.getString("username"), request.getString("password"));

            // Create the request's response with the right session id
            JSONObject response = new JSONObject();
            response.put("sessid", sessid);

            // And send said response
            super.sendResponse(ServerConstants.ResponseCodes.SUCCESS_CODE, response.toString(), httpExchange);

        } catch (LoginException e) {
            // Firstly catch the domain exception

            // just send a invalid user response
            super.sendResponse(ServerConstants.ResponseCodes.INVALID_USER_CODE,
                    ServerConstants.ResponseCodes.INVALID_USER_MESSAGE,
                    httpExchange);

            // Then handle whatever exception happens
        } catch (JSONException e) {
            // This happens on a poorly formed request

            super.sendResponse(ServerConstants.ResponseCodes.POORLY_FORMED_REQUEST_CODE,
                    ServerConstants.ResponseCodes.POORLY_FORMED_REQUEST_MESSAGE,
                    httpExchange);

            InternalServerErrorException ex = new InternalServerErrorException();
            sendResponse(ex.getCode(), ex.getMessage(), httpExchange);

        } catch (Exception e) {
            // Something *bad* happened
            
            AbstractHttpHandler.logger.warn("Something bad happened on login!\n" + e.getMessage());
            sendResponse(ServerConstants.ResponseCodes.INTERNAL_SERVER_ERROR_CODE,
                    ServerConstants.ResponseCodes.INTERNAL_SERVER_ERROR_MESSAGE,
                    httpExchange);
        }
    }
}
