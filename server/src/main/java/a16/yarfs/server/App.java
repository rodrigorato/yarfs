package a16.yarfs.server;

import org.apache.log4j.Logger;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Scanner;

/**
 * entry point to our yarfs server app
 */
public class App {
    private static final Logger logger = Logger.getLogger(App.class);
    private static final int defaultPort = 31000;

    private static int port = defaultPort;
    private static boolean ssl_on = false;

    private static String listenAddress = "";

    public static void main(String[] args) {
        try {
            if(!parseArgs(args)) {
                return;
            }
            logger.info("Starting yarfs server...");

            InetSocketAddress socketAddress;
            if(listenAddress == null || listenAddress.isEmpty()) {
                socketAddress = new InetSocketAddress(port);
            } else {
                InetAddress addr = InetAddress.getByName(listenAddress);
                socketAddress = new InetSocketAddress(addr, port);
            }

            YarfsServer yarfs;
            if(ssl_on) {
                yarfs = new SSLYarfsServer(socketAddress);
            } else {
                yarfs = new YarfsServer(socketAddress);
            }
            logger.info("server started (using "+ yarfs.getClass().getSimpleName() + ")");
        } catch (Exception e){
            logger.error("SERVER CRASH. The crash message is " + e.getMessage(), e);
        }
        logger.debug("main thread exiting");
    }

    private static boolean parseArgs(String[] args) {
        for ( String arg : args) {
            if(arg.equals("--ssl") || arg.equals("--ssl-on")){
                ssl_on = true;
            } else if(arg.equals("--ssl-off")){
                ssl_on = false;
            } else if(arg.equals("--list-sec-providers")){
                System.out.println("Available Security providers and protocols:");
                for(String line : SSLYarfsServer.getSecurityPoviders()) {
                    System.out.println(line);
                }
                System.exit(0);
            } else if(arg.startsWith("--listen=")) {
                listenAddress = arg.replaceFirst("--listen=", "");
            } else if(isInteger(arg)) {
                port = Integer.parseInt(arg);
            } else {
                logger.error("unknown argument: " + arg);
                return false;
            }
        }
        return true;
    }

    /**
     * test if a string matches a base 10 integer
     */
    public static boolean isInteger(String s) {
        int radix = 10;
        Scanner sc = new Scanner(s.trim());
        if(!sc.hasNextInt(radix)) return false;
        // we know it starts with a valid int, now make sure
        // there's nothing left!
        sc.nextInt(radix);
        return !sc.hasNext();
    }
}
