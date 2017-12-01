/***
 * Created by Jorge Heleno
 */
package a16.yarfs.server.handlers;

import a16.yarfs.server.ServerConstants;
import a16.yarfs.server.domain.FileMetadata;
import a16.yarfs.server.domain.Manager;
import a16.yarfs.server.domain.dto.ConcreteFileDto;
import a16.yarfs.server.domain.dto.FileMetadataDto;
import a16.yarfs.server.domain.exceptions.AccessDeniedException;
import a16.yarfs.server.domain.exceptions.InvalidSessionException;
import com.sun.net.httpserver.HttpExchange;
import org.apache.commons.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * This handler answers to the requests of /refresh which
 * are requests used by the client to understand if there is any
 * other new files in the server.
 */
public class RefreshHandler extends AbstractHttpHandler{

    public RefreshHandler(String ... methods){
        super(methods);
    }

    @Override
    public void handle(HttpExchange httpExchange){
        super.handle(httpExchange);

        JSONObject request = null;

        try {
            request = super.getBodyAsJson(httpExchange);

            String sessid = request.getString("sessid");

            // Manager call for refresh
            logger.debug("Starting metadata harvesting");

            // Retrieve Metadata files of the user from the Manager
            List<FileMetadataDto> metadataDtos = Manager.getInstance().getFilesMetadata(sessid);
            logger.debug("Metadata harvesting done. " + metadataDtos.size() + " files found.");

            // Lets start to put everything in json...
            JSONObject response = new JSONObject();
            JSONArray fileList = new JSONArray();
            for(FileMetadataDto fileMetadata : metadataDtos){
                JSONObject fileAttrs = new JSONObject();
                fileAttrs.put("file_id", String.valueOf(fileMetadata.getId()));
                fileAttrs.put("file_name", fileMetadata.getName());
                fileAttrs.put("creation_date", fileMetadata.getCreationDate());
                fileAttrs.put("owner_id", fileMetadata.getOwnerId());
                fileAttrs.put("signature", Base64.encodeBase64(fileMetadata.getSignature()));
                fileList.put(fileAttrs);
            }

            response.put("files", fileList);
            logger.debug("Sending response " + response.toString());
            super.sendResponse(ServerConstants.ResponseCodes.SUCCESS_CODE,
                    response.toString(), httpExchange);


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
            AbstractHttpHandler.logger.error("Something bad happened on refresh!"+System.lineSeparator() +
                    e.getMessage() + System.lineSeparator() + e.getClass(), e);
            sendResponse(ServerConstants.ResponseCodes.INTERNAL_SERVER_ERROR_CODE,
                    ServerConstants.ResponseCodes.INTERNAL_SERVER_ERROR_MESSAGE,
                    httpExchange);
        }

    }
}
