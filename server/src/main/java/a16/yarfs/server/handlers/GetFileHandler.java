/**
 * Created by Jorge Heleno at 28/11/17
 */
package a16.yarfs.server.handlers;

import a16.yarfs.server.ServerConstants;
import a16.yarfs.server.domain.Manager;
import a16.yarfs.server.domain.dto.ConcreteFileDto;
import a16.yarfs.server.domain.exceptions.AccessDeniedException;
import a16.yarfs.server.domain.exceptions.InvalidSessionException;
import com.sun.net.httpserver.HttpExchange;
import org.apache.commons.codec.binary.Base64;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class GetFileHandler
 * Handler that takes care of the get_file endpoint, responsible for delivering files
 * to the clients.
 */
public class GetFileHandler extends AbstractHttpHandler {

    public GetFileHandler(String... methods) {
        super(methods);
    }

    @Override
    public void handle(HttpExchange httpExchange) {
        super.handle(httpExchange);

        JSONObject request = null;

        try {
            request = super.getBodyAsJson(httpExchange);
            logger.info("Received request " + request);
            String sessid = request.getString("sessid");
            String fileId = request.getString("fileid");
            ConcreteFileDto dto = Manager.getInstance().getFile(sessid, fileId);
            JSONObject response = new JSONObject();
            response.put("contents", Base64.encodeBase64String(dto.getContent()));
            //logger.debug("Sending contents "+Base64.encodeBase64String(dto.getContent()));
            response.put("signature", Base64.encodeBase64String(dto.getSignature()));
            response.put("key", Base64.encodeBase64String(dto.getUserKey().getCipheredKey()));
            response.put("filename", dto.getName());
            response.put("owner", dto.getOwnerId());
            response.put("fileid", dto.getId());
            response.put("last_modified_by", dto.getLastModifiedBy());
            super.sendResponse(ServerConstants.ResponseCodes.SUCCESS_CODE,
                    response.toString(), httpExchange);
            //Response:
            // contents - base64
            // signature - base64
            // key - base64
            // filename - String
            // owner - String
        } catch (AccessDeniedException e) {
            super.sendResponse(ServerConstants.ResponseCodes.ACCESS_DENIED_CODE,
                    ServerConstants.ResponseCodes.ACCESS_DENIED_MESSAGE,
                    httpExchange);

        } catch (InvalidSessionException e) {
            super.sendResponse(ServerConstants.ResponseCodes.INVALID_SESSION_ID_CODE,
                    ServerConstants.ResponseCodes.INVALID_SESSION_MESSAGE_MESSAGE,
                    httpExchange);

        } catch (JSONException e) {
            super.sendResponse(ServerConstants.ResponseCodes.POORLY_FORMED_REQUEST_CODE,
                    ServerConstants.ResponseCodes.POORLY_FORMED_REQUEST_MESSAGE,
                    httpExchange);
        } catch (Exception e) {
            AbstractHttpHandler.logger.error("Something bad happened on get file!"+System.lineSeparator() + e.getMessage());            e.printStackTrace();
            sendResponse(ServerConstants.ResponseCodes.INTERNAL_SERVER_ERROR_CODE,
                    ServerConstants.ResponseCodes.INTERNAL_SERVER_ERROR_MESSAGE,
                    httpExchange);
        }

    }
}
