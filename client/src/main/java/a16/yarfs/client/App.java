package a16.yarfs.client;

import a16.yarfs.client.presentation.YarfsShell;
import org.apache.log4j.Logger;

import java.io.IOException;

/**
 * The entry point for our yarfs client
 *
 */
public class App 
{
    private static final Logger logger = Logger.getLogger(App.class);

    public static void main( String[] args )
    {
        if(!parseArgs(args)) {
            System.out.print(getUsage());
            return;
        }
        System.out.println( "Starting yarfs client..." );

        checkHTTPS();

        YarfsShell shell = new YarfsShell(System.in, System.out, true);
        try {
            shell.run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void checkHTTPS() {
        if(!ClientConstants.baseServerUrl.startsWith("https://")) {
            System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
            System.out.println("   Warning: your connection to "+ ClientConstants.baseServerUrl + " is not secure.");
            System.out.println("   You should not send sensible data to it because it could be stolen by attackers.");
            System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
            System.out.println();
            System.out.println("Abort the program now (recommended) or press enter key to continue.");
            try {
                System.in.read();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static boolean parseArgs(String[] args) {
        for ( String arg : args) {
            if(arg.equals("--help")){
                System.out.print(getUsage());
                System.exit(0);
            } else if(arg.startsWith("--ca-addr=")) {
                ClientConstants.CA.address = arg.replaceFirst("--ca-addr=", "");
            } else if(arg.startsWith("--ca-port=")) {
                int port = Integer.parseInt(arg.replaceFirst("--ca-port=", ""));
                ClientConstants.CA.setBasePort(port);
            } else if(arg.startsWith("http")) {
                logger.info("Using base URL " + arg);
                ClientConstants.baseServerUrl = arg;
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
        return "Usage: " + App.class.getSimpleName() + " [OPTIONS] [BASEURL]" + EOL +
                EOL +
                TAB + "BASEURL     " + TAB + "base URL for the yarfs server (default: " + ClientConstants.baseServerUrl + ")" + EOL +
                EOL +
                "OPTIONS:" + EOL +
                TAB + "--ca-addr=<ADDR>" + TAB + "base ADDRess for the CA (default: " + ClientConstants.CA.address + ")" + EOL +
                TAB + "--ca-port=<PORT>" + TAB + "base PORT for the CA (default: "    + ClientConstants.CA.getBasePort() + ")" + EOL +
                TAB + "--help          " + TAB + "print usage information and exit" + EOL +
                TAB + EOL;
    }

}
