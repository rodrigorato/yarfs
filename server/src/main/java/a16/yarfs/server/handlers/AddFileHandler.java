/**
 * Created by nuno at 28/11/17
 */
package a16.yarfs.server.handlers;

import a16.yarfs.server.ServerConstants;
import a16.yarfs.server.domain.Manager;
import a16.yarfs.server.domain.exceptions.InvalidSessionException;
import com.sun.net.httpserver.HttpExchange;
import org.apache.commons.codec.binary.Base64;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class AddFileHandler
 * This handler will handle the endpoint to add files to the server.
 */
// FIXME put the strings into constants like all the others ffs
public final class AddFileHandler extends AbstractHttpHandler {
    public AddFileHandler(String... methods) {
        super(methods);
    }

    @Override
    public void handle(HttpExchange httpExchange) {
        super.handle(httpExchange);

        JSONObject request = null;

        try {
            request = super.getBodyAsJson(httpExchange);
            logger.debug("Received request " + request);

            // Get json arguments
            String sessid = request.getString("sessid");
            String filename = request.getString("filename");

            byte[] fileContent = Base64.decodeBase64(request.getString("file"));
            byte[] signature = Base64.decodeBase64(request.getString("signature"));
            byte[] cipheredKey = Base64.decodeBase64(request.getString("key"));

            AbstractHttpHandler.logger.debug("Received " + request);

            // Add the file and retrieve id to return to the user.
            long fileId = Manager.getInstance().addFile(filename, sessid, fileContent, signature, cipheredKey);
            JSONObject response = new JSONObject();
            response.put("id", fileId);
            super.sendResponse(ServerConstants.ResponseCodes.SUCCESS_CODE, response.toString(), httpExchange);
        } catch (InvalidSessionException e) {
            super.sendResponse(ServerConstants.ResponseCodes.INVALID_SESSION_ID_CODE, ServerConstants.ResponseCodes.INVALID_SESSION_MESSAGE_MESSAGE, httpExchange);
        } catch (JSONException e) {
            super.sendResponse(ServerConstants.ResponseCodes.POORLY_FORMED_REQUEST_CODE, ServerConstants.ResponseCodes.POORLY_FORMED_REQUEST_MESSAGE, httpExchange);
        } catch (Exception e) {
            AbstractHttpHandler.logger.error("Something bad happened on addFile with exception " + e.getClass().toString() + "!\n" + e.getMessage());
            e.printStackTrace();
            sendResponse(ServerConstants.ResponseCodes.INTERNAL_SERVER_ERROR_CODE,
                    ServerConstants.ResponseCodes.INTERNAL_SERVER_ERROR_MESSAGE,
                    httpExchange);
        }
    }
}
