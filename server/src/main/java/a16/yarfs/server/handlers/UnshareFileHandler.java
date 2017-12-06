/**
 * Created by jorge on 06/12/17
 **/
package a16.yarfs.server.handlers;

import a16.yarfs.server.ServerConstants;
import a16.yarfs.server.domain.Manager;
import a16.yarfs.server.domain.exceptions.ShareException;
import com.sun.net.httpserver.HttpExchange;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Class UnshareFileHandler
 * jorge is an IDIOT because he didn't document this class.
 *
 **/
public class UnshareFileHandler extends AbstractHttpHandler{
    public UnshareFileHandler(String... methods) {
        super(methods);
    }

    @Override
    public void handle(HttpExchange httpExchange) {
        super.handle(httpExchange);
        logger.debug("Received request for unshare.");

        try {
            JSONObject request = super.getBodyAsJson(httpExchange);

            String sessid = request.getString("sessid");
            String targetUser = request.getString("target_user");
            String filename = request.getString("filename");
            Manager.getInstance().unshareFile(sessid, filename, targetUser);
            JSONObject response = new JSONObject();
            response.put("success", true);
            super.sendResponse(ServerConstants.ResponseCodes.SUCCESS_CODE, response.toString(), httpExchange);

        } catch (ShareException e){
            super.sendResponse(ServerConstants.ResponseCodes.SHARE_ERROR_CODE,
                    ServerConstants.ResponseCodes.SHARE_ERROR_MESSAGE, httpExchange);
        } catch (IOException | ClassNotFoundException e) {
            super.sendResponse(ServerConstants.ResponseCodes.INTERNAL_SERVER_ERROR_CODE,
                    ServerConstants.ResponseCodes.INTERNAL_SERVER_ERROR_MESSAGE, httpExchange);
        }

    }
}
