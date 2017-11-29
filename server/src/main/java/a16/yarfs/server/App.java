package a16.yarfs.server;

import a16.yarfs.server.handlers.*;
import com.sun.net.httpserver.HttpServer;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * Hello world!
 */
public class App {
    // private final Logger log = Logger.getLogger(App.class);
    private static final int defaultPort = 31000;

    public static void main(String[] args) {
        Logger logger = Logger.getLogger(App.class);
        try {
            logger.info("Starting yarfs server...");
            if (args.length > 0) {
                (new App()).start(Integer.parseInt(args[0]));
            } else {
                (new App()).start(defaultPort);
            }
        } catch (Exception e){
            logger.error("SERVER CRASH. The crash message is " + e.getMessage(), e);
        }
    }

    public void start(int port) {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
            configure(server);
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void configure(HttpServer server) {
        server.createContext(ServerConstants.Endpoints.ADD_FILE, new AddFileHandler("POST"));
        server.createContext(ServerConstants.Endpoints.LIST_FILES, new ListFilesHandler());
        server.createContext(ServerConstants.Endpoints.LIST_USERS, new ListUsersHandler("GET", "POST"));
        server.createContext(ServerConstants.Endpoints.LOGIN, new UserLoginHandler("POST"));
        server.createContext(ServerConstants.Endpoints.LOGOUT, new UserLogoutHandler("POST"));
        server.createContext(ServerConstants.Endpoints.REGISTER, new UserRegisterHandler("POST"));
        server.createContext(ServerConstants.Endpoints.DELETE_FILE, new DeleteFileHandler());
        server.createContext(ServerConstants.Endpoints.SHARE_FILE, new ShareFileHandler());
        server.createContext(ServerConstants.Endpoints.ECHO, new EchoHandler("GET", "POST"));
    }

}
