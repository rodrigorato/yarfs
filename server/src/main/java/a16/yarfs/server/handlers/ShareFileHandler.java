/**
 * Created by nuno at 28/11/17
 */
package a16.yarfs.server.handlers;

import a16.yarfs.server.ServerConstants;
import a16.yarfs.server.domain.Manager;
import a16.yarfs.server.domain.exceptions.ShareException;
import a16.yarfs.server.exception.http.InternalServerErrorException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.apache.commons.codec.binary.Base64;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Class ShareFileHandler
 * nuno is an IDIOT because it hasn't made documentation for this class.
 */
public final class ShareFileHandler extends AbstractHttpHandler {

    public ShareFileHandler(String ... methods){
        super(methods);
    }

    @Override
    public void handle(HttpExchange httpExchange) {
        super.handle(httpExchange);
        JSONObject response = new JSONObject();
        try {
            JSONObject request = super.getBodyAsJson(httpExchange);
            String sessid = request.getString("sessid");
            byte[] userKey = Base64.decodeBase64(request.getString("user_key"));
            String targetUser = request.getString("target_user");
            long fileId = request.getLong("file_id");
            Manager.getInstance().shareFile(sessid, fileId, userKey, targetUser);
            response.put("success", true);

        } catch (ShareException e){
            logger.info("Error sharing file", e);
            response.put("success", false);
        } catch (Exception e){
            logger.error("Something bad happened!", e);
            sendResponse(ServerConstants.ResponseCodes.INTERNAL_SERVER_ERROR_CODE,
                    ServerConstants.ResponseCodes.INTERNAL_SERVER_ERROR_MESSAGE,
                    httpExchange);
            response.put("success", false);
        }
        super.sendResponse(ServerConstants.ResponseCodes.SUCCESS_CODE,
                response.toString(), httpExchange);
    }
}
