package a16.yarfs.ca;

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
        System.out.println( "Starting yarfs ca..." );
    }

    private static boolean parseArgs(String[] args) {
        for ( String arg : args) {
            if(arg.equals("--help")){
                System.out.print(getUsage());
                System.exit(0);
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
        return "Usage: " + App.class.getSimpleName();
    }
}
