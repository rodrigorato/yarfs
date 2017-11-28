/**
 * Created by nuno at 28/11/17
 */
package a16.yarfs.server.handlers;

import a16.yarfs.server.exception.http.HttpException;
import com.sun.net.httpserver.HttpExchange;

/**
 * Class EchoHandler
 * nuno is an IDIOT because it hasn't made documentation for this class.
 */
public final class EchoHandler extends AbstractHttpHandler {
    public EchoHandler(String... methods) {
        super(methods);
    }

    @Override
    public void handle(HttpExchange httpExchange) {
        super.handle(httpExchange);
        try {
            sendResponse(200, "Echo " + getBody(httpExchange), httpExchange);
        } catch (HttpException e) {
            sendResponse(e.getCode(), e.getMessage(), httpExchange);
        }

    }


}
