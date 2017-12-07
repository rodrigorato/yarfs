package a16.yarfs.ca;


/**
 * Class CAConstants
 * stores constants to be used globally by the CA application
 */
public class CAConstants {

    /** default base URL of the yarfs server. Can be overridden by App args */
    public static String baseServerUrl = "https://server.yarfs:31000";
    public static int connectTimeout = 5000; // ms
    public static String listenAddr  = "0.0.0.0";

    public static final class Endpoints {
        // Endpoing to authenticate a user with a given session token
        public static final String AUTHENTICATE = "/authenticate";
    }

    private static int basePort = 31001;
    public static void setBasePort(int port) {
        basePort = port;
    }

    public static int getBasePort() {
        return basePort;
    }

    public static final class PublishService {
        public static int getPort() {
            return basePort;
        }
        public static final String SYMMETRIC_CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";
        public static final int CHALLENGE_SIZE = 128;
    }

    public static final class RequestService {
        public static int getPort() {
            return basePort + 1;
        }
    }

    public static final class Keys {
        public static final String KEYSTORE_FILE = "yarfs-ca.jks";
        public static final String KEYSTORE_PASSWORD = "password";
        public static final String CERTIFICATE_ALIAS = "ca";
        public static final String PRIVATE_KEY_ALIAS = "ca";
        public static final String PRIVATE_KEY_PASSWORD = "password";


        public static final String CA_KEYSTORE_CIPHER = "RSA";
    }
}
