package a16.yarfs.server.handlers;

import a16.yarfs.server.ServerConstants;
import a16.yarfs.server.domain.Manager;
import com.sun.net.httpserver.HttpExchange;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class AuthenticateHandler
 * <p>
 * Used when a CA tries to authenticate a user/session
 */
public final class AuthenticateHandler extends AbstractHttpHandler {

    public AuthenticateHandler(String... methods) {
        super(methods);
    }

    @Override
    public void handle(HttpExchange httpExchange) {
        super.handle(httpExchange);

        JSONObject request = null;
        try {
            // Get the request to read the parameters
            request = super.getBodyAsJson(httpExchange);

            // Use those parameters to authenticate (or not) the session with the Manager
            logger.debug("Trying to authenticate " + request.getString("username"));
            boolean isAuthenticated = Manager.getInstance()
                    .authenticateSession(request.getString("sessionid"),
                                         request.getString("username"));

            // Create the response
            JSONObject response = new JSONObject();
            response.put("authenticated", isAuthenticated);
            response.put("username", request.getString("username"));
            response.put("sessionid", request.getString("sessionid"));

            // Send the response
            super.sendResponse(ServerConstants.ResponseCodes.SUCCESS_CODE,
                               response.toString(),
                               httpExchange);

        } catch (JSONException e) {
            // This happens on a poorly formed request

            super.sendResponse(ServerConstants.ResponseCodes.POORLY_FORMED_REQUEST_CODE,
                    ServerConstants.ResponseCodes.POORLY_FORMED_REQUEST_MESSAGE,
                    httpExchange);

        } catch (Exception e) {
            // Something *bad* happened

            AbstractHttpHandler.logger.error("Something bad happened on login!\n" + e.getMessage());
            sendResponse(ServerConstants.ResponseCodes.INTERNAL_SERVER_ERROR_CODE,
                    ServerConstants.ResponseCodes.INTERNAL_SERVER_ERROR_MESSAGE,
                    httpExchange);
        }

    }
}
