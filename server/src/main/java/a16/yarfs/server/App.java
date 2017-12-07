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
    private static boolean ssl_on = true;

    private static String listenAddress = "";

    public static void main(String[] args) {
        try {
            if(!parseArgs(args)) {
                System.out.print(getUsage());
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
            } else if(arg.equals("--help")){
                System.out.print(getUsage());
                System.exit(0);
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

    public static String getUsage() {
        String EOL = System.lineSeparator();
        String TAB = "\t";
        return "Usage: " + App.class.getSimpleName() + " [OPTIONS] <PORT>" + EOL +
                EOL +
                TAB + "PORT"     + TAB + "accept HTTP(S) connections on PORT port" + EOL +
                EOL +
                "OPTIONS:" + EOL +
                TAB + "--help              " + TAB + "print usage information and exit" + EOL +
                TAB + "--ssl,--ssl-on      " + TAB + "enable HTTP over SSL/TLS using " + ServerConstants.SSL_PROTOCOL + EOL +
                TAB + "--ssl-off           " + TAB + "use plain HTTP without SSL" + EOL +
                TAB + "--listen=<ADDR>     " + TAB + "accept HTTP(S) connections only on ADDR" + EOL +
                TAB + "--list-sec-providers" + TAB + "list available Java security providers" + EOL +
                TAB + EOL;
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
