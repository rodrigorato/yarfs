/**
 * Created by nuno at 28/11/17
 */
package a16.yarfs.server.handlers;

import a16.yarfs.server.domain.Manager;
import a16.yarfs.server.exception.http.BadRequestException;
import a16.yarfs.server.exception.http.InternalServerErrorException;
import com.sun.net.httpserver.HttpExchange;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Class ListUsersHandler
 * nuno is an IDIOT because it hasn't made documentation for this class.
 */
public final class ListUsersHandler extends AbstractHttpHandler {

    public ListUsersHandler(String ... methods){
        super(methods);
    }

    public void handle(HttpExchange httpExchange) {
        super.handle(httpExchange);
        JSONObject jsonObject = null;

        try {
            jsonObject = super.getBodyAsJson(httpExchange);
            JSONObject response = new JSONObject();
            response.put("users", Manager.getInstance().listUsers());
            sendResponse(200, response.toString(), httpExchange);

        } catch (IOException e) {
            //e.printStackTrace();
            BadRequestException ex = new BadRequestException();
            super.sendResponse(ex.getCode(), ex.getMessage(), httpExchange);

        } catch (JSONException e) {
            //e.printStackTrace();
            InternalServerErrorException ex = new InternalServerErrorException();
            super.sendResponse(ex.getCode(), ex.getMessage(), httpExchange);
        }


        //TODO
    }
}
