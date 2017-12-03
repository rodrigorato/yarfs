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
        YarfsShell shell = new YarfsShell(System.in, System.out, true);
        try {
            shell.run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean parseArgs(String[] args) {
        for ( String arg : args) {
            if(arg.equals("--help")){
                System.out.print(getUsage());
                System.exit(0);
            } else if(arg.startsWith("http")) {
                logger.info("Using base URL " + arg);
                ClientConstants.baseUrl = arg;
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
                TAB + "BASEURL     " + TAB + "base URL for the yarfs server (default: " + ClientConstants.baseUrl + ")" + EOL +
                EOL +
                "OPTIONS:" + EOL +
                TAB + "--help      " + TAB + "print usage information and exit" + EOL +
                TAB + EOL;
    }

}
