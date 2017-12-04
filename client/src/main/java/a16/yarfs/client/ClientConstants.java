/**
 * Created by nuno at 28/11/17
 */
package a16.yarfs.client;


/**
 * Class ClientConstants
 * stores constants to be used globally by the client application
 */
public class ClientConstants {

    /** default base URL of the yarfs server. Can be overridden by App args */
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

        /// refresh local database of metadata files
        public final static String REFRESH = "/refresh";

        /// echo (stuff) : stuff
        public final static String ECHO = "/echo"; //echo endpoint for tests
    }

    public static final class StorageStandards{
        // Folder which is the base for all the subfolders of the client
        public final static String BASE_FOLDER = "/var/yarfs/client/";
        // Folder for files
        public final static String FILE_FOLDER = BASE_FOLDER + "/files/";
        //Folder for keys
        public final static String KEY_FOLDER = BASE_FOLDER + "/.keys/";
        // Suffix for all metadata files
        public final static String FILE_METADATA_SUFFIX = ".metadata";
        // Prefix for all metadata files
        public final static String FILE_METADATA_PREFIX = "";
    }


    public static final class KeyStandards {

        // Size of asymmetric keys, in bits
        public final static int ASYMMETRIC_KEY_SIZE = 2048;

        // Algorithm to be used by asymmetric keys
        public final static String ASYMMETRIC_ALGORITHM = "RSA";

        // Symmetric algorithm to be used by symmetric keys
        public final static String SYMMETRIC_ALGORITHM = "AES";

        // Block mode used.
        public final static String BLOCK_MODE = "ECB";

        // Padding mode used.
        public final static String PADDING_SCHEME = "PKCS5Padding";

        // Standard used by symmetric keys. Includes algorithm, block mode and padding mode.
        public final static String SYMMETRIC_STANDARD = SYMMETRIC_ALGORITHM + "/" + BLOCK_MODE + "/" + PADDING_SCHEME;

        // Size of the Symmetric keys, in bytes
        public final static int SYMMETRIC_KEY_SIZE = 32; // This gives a key of 32 byte * 8 = 256 bit

        // Suffix used in the files of private keys
        public final static String ASYMMETRIC_PRIVATE_SUFFIX = ".priv";

        // Suffix using in the files of public keys
        public final static String ASYMMETRIC_PUBLIC_SUFFIX = ".pub";

    }
}
