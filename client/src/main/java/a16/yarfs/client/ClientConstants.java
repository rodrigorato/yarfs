/**
 * Created by nuno at 28/11/17
 */
package a16.yarfs.client;

/**
 * Class ClientConstants
 * stores constants to be used globally by the client application
 */
public class ClientConstants {

    // FIXME: put this somewhere else, e.g. config file or app argument
    public static String baseUrl = "http://127.0.0.1:31000";
    public static int connectTimeout = 5000; // ms

    public static final class Endpoints {
        /// register (username, password) : OK | NOK
        public final static String REGISTER = "/register";

        /// login (username, password) : SESSID | NOK
        public final static String LOGIN = "/login";

        /// logout (SESSID) : OK | NOK (doesnt matter)
        public final static String LOGOUT = "/logout";

        /// users (SESSID) : {'users': [username...]}
        public final static String LIST_USERS = "/users";

        /// add_file (SESSID, filename, filecontent, filesignature, ciphered_simmetric_key) : file_id
        public final static String ADD_FILE = "/add_file";

        /// delete_file (SESSID, file_id) : OK | NOK
        public final static String DELETE_FILE = "/delete_file";

        /// list_files (SESSID, username) : {filenames... : fileids..., size, whatever}
        public final static String LIST_FILES = "/list_files";

        /// share_file (SESSID, fileid, target_user, target_user_key) : OK | NOK
        public final static String SHARE_FILE = "/share_file";

        /// unshare_file (SESSID, fileid, targeuser) : OK | NOK
        public final static String UNSHARE_FILE = "/unshare_file";

        /// get_file (SESSID, fileid) : (filename, filecontent, filesignature, user_ciphered_simmetric_key)
        public final static String GET_FILE = "/get_file";

        /// echo (stuff) : stuff
        public final static String ECHO = "/echo"; //echo endpoint for tests
    }
}
