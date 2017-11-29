package a16.yarfs.server.handlers;

import a16.yarfs.server.ServerConstants;
import a16.yarfs.server.domain.Manager;
import a16.yarfs.server.domain.exceptions.DuplicatedUsernameException;
import com.sun.net.httpserver.HttpExchange;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class UserRegisterHandles
 * <p>
 * Used when a register request is being handled
 */
public final class UserRegisterHandler extends AbstractHttpHandler {

    public UserRegisterHandler(String... methods) {
        super(methods);
    }

    @Override
    public void handle(HttpExchange httpExchange) {
        super.handle(httpExchange);

        JSONObject request = null;
        try {
            // Get the request to read the username and password parameters
            request = super.getBodyAsJson(httpExchange);

            // Use those parameters to register (or not) a user with the Manager
            Manager.getInstance().registerUser(request.getString("username"),
                    request.getString("password"));

            // Create the request's response with the right session id
            /* FIXME should depend on manager */
            JSONObject response = ServerConstants.DefaultResponses.getOkResponse();

            // And send said response
            super.sendResponse(ServerConstants.ResponseCodes.SUCCESS_CODE, response.toString(), httpExchange);

        } catch (DuplicatedUsernameException e) {
            // Firstly catch the domain exception

            // just send a invalid user response
            super.sendResponse(ServerConstants.ResponseCodes.DUPLICATE_USER_CODE,
                    ServerConstants.ResponseCodes.DUPLICATE_USER_MESSAGE,
                    httpExchange);

            // Then handle whatever exception happens
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
