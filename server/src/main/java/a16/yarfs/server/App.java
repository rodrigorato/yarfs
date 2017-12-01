package a16.yarfs.server;

import org.apache.log4j.Logger;

/**
 * entry point to our yarfs server app
 */
public class App {
    private static final Logger logger = Logger.getLogger(App.class);
    private static final int defaultPort = 31000;

    public static void main(String[] args) {
        try {
            logger.info("Starting yarfs server...");
            if (args.length > 0) {
                int port = Integer.parseInt(args[0]);
                new YarfsServer(port);
            } else {
                new YarfsServer(defaultPort);
            }
        } catch (Exception e){
            logger.error("SERVER CRASH. The crash message is " + e.getMessage(), e);
        }
        logger.debug("main thread exiting");
    }
}
