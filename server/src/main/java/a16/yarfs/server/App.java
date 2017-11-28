package a16.yarfs.server;

import a16.yarfs.server.domain.ServerConstants;
import a16.yarfs.server.handlers.*;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;

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

        System.out.println( "Starting yarfs server..." );
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
        server.createContext(ServerConstants.Endpoints.LIST_USERS, new ListUsersHandler("GET","POST"));
        server.createContext(ServerConstants.Endpoints.LOGIN, new UserLoginHandler("POST"));
        server.createContext(ServerConstants.Endpoints.LOGOUT, new UserLogoutHandler());
        server.createContext(ServerConstants.Endpoints.REGISTER, new UserRegisterHandler("POST"));
        server.createContext(ServerConstants.Endpoints.REMOVE_FILE, new RemoveFileHandler());
        server.createContext(ServerConstants.Endpoints.SHARE_FILE, new ShareFileHandler());
        server.createContext(ServerConstants.Endpoints.ECHO, new EchoHandler("GET", "POST"));
    }

}
