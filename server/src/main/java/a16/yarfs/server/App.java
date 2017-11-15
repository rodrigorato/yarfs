package a16.yarfs.server;

import a16.yarfs.server.domain.Manager;
import a16.yarfs.server.domain.ServerConstants;
import a16.yarfs.server.exception.http.BadRequestException;
import a16.yarfs.server.exception.http.HttpException;
import a16.yarfs.server.exception.http.InternalServerErrorException;
import a16.yarfs.server.exception.http.MethodNotAllowedException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpsServer;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.logging.FileHandler;

/**
 * Hello world!
 *
 */
public class App 
{
   // private final Logger log = Logger.getLogger(App.class);
    private static final int defaultPort = 31000;
    public static void main( String[] args )
    {
        if( args.length > 0){
            (new App()).start(Integer.parseInt(args[0]));
        }else{
            (new App()).start(defaultPort);
        }
    }

    public void start(int port){
     //   log.info("Starting server...");
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(port),0);
            configure(server);
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void configure(HttpServer server){
        server.createContext(ServerConstants.Endpoints.ADD_FILE, new AddFileHandler());
        server.createContext(ServerConstants.Endpoints.LIST_FILES, new ListFilesHandler());
        server.createContext(ServerConstants.Endpoints.LIST_USERS, new ListUsersHandler());
        server.createContext(ServerConstants.Endpoints.LOGIN, new UserLoginHandler());
        server.createContext(ServerConstants.Endpoints.LOGOUT, new UserLogoutHandler());
        server.createContext(ServerConstants.Endpoints.REGISTER, new UserRegisterHandler("POST"));
        server.createContext(ServerConstants.Endpoints.REMOVE_FILE, new RemoveFileHandler());
        server.createContext(ServerConstants.Endpoints.SHARE_FILE, new ShareFileHandler());
        server.createContext(ServerConstants.Endpoints.ECHO, new EchoHandler("GET", "POST"));
    }

    private abstract class AbstractHttpHandler implements HttpHandler{

        private List<String> supportedMethods;

        protected AbstractHttpHandler(String ... methods){
            supportedMethods = new ArrayList<String>();
            supportedMethods.addAll(Arrays.asList(methods));
        }

        public void handle(HttpExchange httpExchange){
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
            while((buffer=br.readLine()) != null){
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

        protected String getBody(HttpExchange httpExchange){
            BufferedReader br = new BufferedReader(new InputStreamReader(httpExchange.getRequestBody()));
            String request = "";
            String buffer;
            try {
                while((buffer=br.readLine()) != null){
                    request += buffer;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return request;
        }

        protected void sendResponse(int code, String response, HttpExchange httpExchange){
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

    private final class EchoHandler extends AbstractHttpHandler{
        public EchoHandler(String ... methods){
            super(methods);
        }

        @Override
        public void handle(HttpExchange httpExchange){
            super.handle(httpExchange);
            try {
                sendResponse(200,"Echo " +getBody(httpExchange), httpExchange);
            } catch (HttpException e){
                sendResponse(e.getCode(), e.getMessage(), httpExchange);
            }

        }


    }

    private final class UserRegisterHandler extends AbstractHttpHandler{

        public UserRegisterHandler(String ...  methods){
            super(methods);
        }

        @Override
        public void handle(HttpExchange httpExchange) {
//            log.debug("Handling register request");
            //if(!httpExchange.getRequestMethod().equals("POST")){
             //   throw new UnsupportedOperationException();
            //}
            JSONObject body = null;
            try {
                super.handle(httpExchange);
                body = super.getBodyAsJson(httpExchange);
                Manager.getInstance().registerUser(body.getString("username"), body.getString("password"));
                sendResponse(200,"This is test "+body, httpExchange);
            } catch (IOException e) {
                e.printStackTrace(); // FIXME: Do something useful here
                //System.out.println("Body is "+body);
            } catch (HttpException e){
                    sendResponse(e.getCode(),e.getMessage(),httpExchange);
            } catch (JSONException e) {
                //e.printStackTrace();
                BadRequestException bre = new BadRequestException();
                sendResponse(bre.getCode(), bre.getMessage(), httpExchange);
            }
        }
    }

    private final class UserLoginHandler implements HttpHandler{

        public void handle(HttpExchange httpExchange) throws IOException {
            //TODO
        }
    }

    private final class UserLogoutHandler implements HttpHandler{

        public void handle(HttpExchange httpExchange) throws IOException {
            //TODO
        }
    }

    private final class ListUsersHandler implements HttpHandler{

        public void handle(HttpExchange httpExchange) throws IOException {
            //TODO
        }
    }

    private final class UserAttributesHandler implements HttpHandler{

        public void handle(HttpExchange httpExchange) throws IOException {
            //TODO
        }
    }

    private final class AddFileHandler implements  HttpHandler{

        public void handle(HttpExchange httpExchange) throws IOException {
            //TODO
        }
    }

    private final class RemoveFileHandler implements HttpHandler{

        public void handle(HttpExchange httpExchange) throws IOException {
            //TODO
        }
    }

    private final class ListFilesHandler implements HttpHandler{

        public void handle(HttpExchange httpExchange) throws IOException {
            //TODO
        }
    }

    private final class ShareFileHandler implements HttpHandler{

        public void handle(HttpExchange httpExchange) throws IOException {
            //TODO
        }
    }
}
