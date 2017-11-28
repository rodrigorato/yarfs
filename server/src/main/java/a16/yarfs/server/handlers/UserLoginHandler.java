/**
 * Created by nuno at 28/11/17
 */
package a16.yarfs.server.handlers;

import a16.yarfs.server.domain.Manager;
import a16.yarfs.server.exception.http.BadRequestException;
import a16.yarfs.server.exception.http.HttpException;
import a16.yarfs.server.exception.http.InternalServerErrorException;
import com.sun.net.httpserver.HttpExchange;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Class UserLoginHandler
 * nuno is an IDIOT because it hasn't made documentation for this class.
 */
public final class UserLoginHandler extends AbstractHttpHandler {

    public UserLoginHandler(String ... methods) {
        super(methods);
    }

    public void handle(HttpExchange httpExchange) {
        super.handle(httpExchange);

        JSONObject user = null;
        try {
            user = super.getBodyAsJson(httpExchange);
            String sessid = Manager.getInstance().loginUser(user.getString("username"),user.getString("password"));
            JSONObject response = new JSONObject();
            response.put("sessid",sessid);
            super.sendResponse(200,response.toString(),httpExchange);
        } catch (IOException e) {
            //e.printStackTrace();
            InternalServerErrorException ex = new InternalServerErrorException();
            sendResponse(ex.getCode(), ex.getMessage(), httpExchange);
        } catch (JSONException e) {
            //e.printStackTrace();
            BadRequestException ex = new BadRequestException();
            sendResponse(ex.getCode(), ex.getMessage(), httpExchange);
        } catch (HttpException e){
            super.sendResponse(e.getCode(), e.getMessage(), httpExchange);
        }

        //TODO
    }
}
