package a16.yarfs.server;

/**
 * This class is meant to be used to store
 * constants used on the Server application.
 * <p>
 * It is final as it cannot be derived from.
 * It's constructor is private as no one should
 * ever call it.
 * <p>
 * Created by Rodrigo Rato on 11/13/17.
 */
public final class ServerConstants {
    /**
     * This is the directory where all the server files should be stored.
     */
    public final static String SERVER_DIRECTORY = "/var/yarfs/server";

    /**
     * Digest algorithm used to hash user passwords.
     *
     * @see https://docs.oracle.com/javase/8/docs/technotes/guides/security/StandardNames.html#MessageDigest
     */
    public static final String USER_PASSWD_DIGEST = "SHA-256";

    /**
     * how long should a Session be valid for
     */
    public static final long SESSION_DURATION_HOURS = 3;

    /**
     * Constants for the usage of the file system.
     * This means all directories should be declared here.
     */
    public static final class FileSystem {
        public final static String FILES_DIRECTORY = ServerConstants.SERVER_DIRECTORY + "/files";

        /**
         * Builds the path where a certain file with fileName should be stored
         *
         * @param fileName is the name of the file to store
         * @return the path where the file should be stored
         */
        public final static String buildFileStoragePath(String fileName) {
            return FileSystem.FILES_DIRECTORY + "/" + fileName;
        }
    }

    /**
     * Class used for endpoints of the http server.
     */
    public static final class Endpoints {
        public final static String REGISTER = "/register";
        public final static String LOGIN = "/login";
        public final static String LOGOUT = "/logout";
        public final static String LIST_USERS = "/user";
        public final static String ADD_FILE = "/addFile";
        public final static String DELETE_FILE = "/deleteFile";
        public final static String LIST_FILES = "/listFiles";
        public final static String SHARE_FILE = "/shareFile";
        public final static String ECHO = "/echo"; //echo endpoint for tests
    }

    /**
     * Class used for http response codes.
     */
    public static final class ResponseCodes {
        public final static int SUCCESS_CODE = 200;
        public final static String SUCCESS_MESSAGE = "Success";

        public final static int POORLY_FORMED_REQUEST_CODE = 400;
        public final static String POORLY_FORMED_REQUEST_MESSAGE = "Poorly formed request";

        public final static int INTERNAL_SERVER_ERROR_CODE = 500;
        public final static String INTERNAL_SERVER_ERROR_MESSAGE = "Internal Server Error";

        public final static int DUPLICATE_USER_CODE = 601;
        public final static String DUPLICATE_USER_MESSAGE = "Duplicate user";

        public final static int INVALID_USER_CODE = 602;
        public final static String INVALID_USER_MESSAGE = "Invalid user/password";


    }


    /**
     * This class shouldn't be instantiated
     * or derived from. Ever.
     * <p>
     * The exception guarantees that no one, not
     * even with reflection, can instantiate it.
     */
    private ServerConstants() {
        throw new UnsupportedOperationException();
    }
}
