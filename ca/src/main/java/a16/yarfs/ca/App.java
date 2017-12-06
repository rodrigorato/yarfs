package a16.yarfs.ca;

import a16.yarfs.ca.handlers.exceptions.KeyStoreException;
import org.apache.log4j.Logger;


/**
 * The entry point for our yarfs ca
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
        System.out.println( "Starting yarfs CA..." );

        try {
            new CAServer();

        } catch (KeyStoreException e) {
            logger.error(e.getMessage());

        }

    }

    private static boolean parseArgs(String[] args) {
        for ( String arg : args) {
            if(arg.equals("--help")){
                System.out.print(getUsage());
                System.exit(0);
            } else if(arg.startsWith("http")) {
                logger.info("Using base server URL " + arg);
                CAConstants.baseServerUrl = arg;
            } else if(arg.startsWith("--port=")) {
                int port = Integer.parseInt(arg.replaceFirst("--port=", ""));
                CAConstants.setBasePort(port);
            } else if(arg.startsWith("--listen=")) {
                CAConstants.listenAddr = arg.replaceFirst("--listen=", "");
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
                TAB + "BASEURL     " + TAB + "base URL for the yarfs server (default: " + CAConstants.baseServerUrl + ")" + EOL +
                EOL +
                "OPTIONS:" + EOL +
                TAB + "--listen=<ADDR>" + TAB + "accept connections only on ADDR (default: " + CAConstants.listenAddr + ")" + EOL +
                TAB + "--port=<PORT>" + TAB + "base PORT for the CA (default: "    + CAConstants.getBasePort() + ")" + EOL +
                TAB + "--help      " + TAB + "print usage information and exit" + EOL +
                TAB + EOL;
    }

}

