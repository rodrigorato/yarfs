/**
 * Created by nuno at 28/11/17
 */
package a16.yarfs.server.handlers;

import a16.yarfs.server.exception.http.BadRequestException;
import a16.yarfs.server.exception.http.InternalServerErrorException;
import a16.yarfs.server.exception.http.MethodNotAllowedException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Class AbstractHttpHandler
 * nuno is an IDIOT because it hasn't made documentation for this class.
 */
public abstract class AbstractHttpHandler implements HttpHandler {

    private List<String> supportedMethods;
    protected static Logger logger = Logger.getLogger(AbstractHttpHandler.class);

    protected AbstractHttpHandler(String... methods) {
        supportedMethods = new ArrayList<String>();
        supportedMethods.addAll(Arrays.asList(methods));
    }

    @Override
    public void handle(HttpExchange httpExchange) {
        for (String supportedMethod : supportedMethods) { //Could be done with iterator but small method list
            if (httpExchange.getRequestMethod().equalsIgnoreCase(supportedMethod)) {
                return;
            }
        }
        throw new MethodNotAllowedException();
    }

    protected JSONObject getBodyAsJson(HttpExchange httpExchange) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(httpExchange.getRequestBody()));
        String request = "";
        String buffer;
        while ((buffer = br.readLine()) != null) {
            request += buffer;
        }
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(request);
        } catch (JSONException e) {
//                e.printStackTrace();
            throw new BadRequestException();
        }
        return jsonObject;
    }

    protected String getBody(HttpExchange httpExchange) {
        BufferedReader br = new BufferedReader(new InputStreamReader(httpExchange.getRequestBody()));
        String request = "";
        String buffer;
        try {
            while ((buffer = br.readLine()) != null) {
                request += buffer;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return request;
    }

    protected void sendResponse(int code, String response, HttpExchange httpExchange) {
        try {
            httpExchange.sendResponseHeaders(code, response.length());
            OutputStream os = httpExchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        } catch (IOException e) {
            //e.printStackTrace();
            throw new InternalServerErrorException();
        }

    }

}
