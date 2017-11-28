/**
 * Created by nuno at 28/11/17
 */
package a16.yarfs.client;

/**
 *  Class ClientConstants
 *  stores constants to be used globally by the client application
 */
public class ClientConstants {

    // FIXME: put this somewhere else, e.g. config file or app argument
    public static String baseUrl = "http://127.0.0.1:31000";

    public static final class Endpoints{
        public final static String REGISTER = "/register";
        public final static String LOGIN = "/login";
        public final static String LOGOUT = "/logout";
        public final static String LIST_USERS = "/user";
        public final static String ADD_FILE = "/addFile";
        public final static String REMOVE_FILE = "/removeFile";
        public final static String LIST_FILES = "/listFiles";
        public final static String FILE_DETAILS = "/fileDetails";
        public final static String SHARE_FILE = "/shareFile";
        public final static String ECHO = "/echo"; //echo endpoint for tests
    }
}
