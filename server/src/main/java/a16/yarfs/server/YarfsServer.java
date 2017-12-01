package a16.yarfs.server;

import a16.yarfs.server.handlers.*;
import com.sun.net.httpserver.HttpServer;
import org.apache.log4j.Logger;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * YarfsServer
 */
public class YarfsServer {
    private final Logger logger = Logger.getLogger(YarfsServer.class);

    public YarfsServer(int port) throws IOException {
        this(port, false);
    }

    public YarfsServer(int port, boolean ssl) throws IOException {
        InetSocketAddress address = new InetSocketAddress(port);
        HttpServer server;
        if(ssl) {
            // TODO SSL
            throw new NotImplementedException();
        } else {
            server = HttpServer.create(address, 0);
        }
        configure(server);
        server.start();

        address = server.getAddress();
        logger.info("listening on " + address.toString());
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
        server.createContext(ServerConstants.Endpoints.GET_FILE, new GetFileHandler("POST"));
        server.createContext(ServerConstants.Endpoints.REFRESH, new RefreshHandler("POST"));
        server.createContext(ServerConstants.Endpoints.ECHO, new EchoHandler("GET", "POST"));
    }

}
