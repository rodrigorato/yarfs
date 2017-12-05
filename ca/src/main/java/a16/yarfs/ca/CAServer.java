package a16.yarfs.ca;

import com.sun.net.httpserver.HttpServer;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;

public class CAServer {

    private static final Logger logger = Logger.getLogger(CAServer.class);

    public CAServer(InetSocketAddress address) throws IOException {
        HttpServer server;

        server = createHttpServer(address, 0);

        configure(server);
        server.start();

        address = server.getAddress();
        logger.info("listening on " + address.toString());
    }


    private void configure(HttpServer server) {
    }

    protected HttpServer createHttpServer(InetSocketAddress address, int backlog) throws IOException {
        logger.debug("Using HttpServer from " + this.getClass().getCanonicalName());
        return HttpServer.create(address, backlog);
    }

}
