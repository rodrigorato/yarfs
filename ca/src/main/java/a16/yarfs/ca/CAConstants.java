package a16.yarfs.ca;


/**
 * Class CAConstants
 * stores constants to be used globally by the CA application
 */
public class CAConstants {

    /** default base URL of the yarfs server. Can be overridden by App args */
    public static String baseUrl = "http://127.0.0.1:31000";
    public static int connectTimeout = 5000; // ms

    public static final class Endpoints {
        // Endpoing to authenticate a user with a given session token
        public static final String AUTHENTICATE = "/authenticate";
    }
}
