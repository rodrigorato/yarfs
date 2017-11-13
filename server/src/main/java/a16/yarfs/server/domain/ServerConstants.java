package a16.yarfs.server.domain;

/**
 * This class is meant to be used to store
 * constants used on the Server application.
 *
 * It is final as it cannot be derived from.
 * It's constructor is private as no one should
 * ever call it.
 *
 * Created by Rodrigo Rato on 11/13/17.
 */
public final class ServerConstants {
    /**
     * This is the directory where all the server files should be stored.
     */
    public final static String SERVER_DIRECTORY = "/var/yarfs/server";

    /**
     * Digest algorithm used to hash user passwords.
     * @see https://docs.oracle.com/javase/8/docs/technotes/guides/security/StandardNames.html#MessageDigest
     */
    public static final String USER_PASSWD_DIGEST = "SHA-256";

    /**
     * Constants for the usage of the file system.
     * This means all directories should be declared here.
     */
    public static final class FileSystem {
        public final static String FILES_DIRECTORY = ServerConstants.SERVER_DIRECTORY + "/files";

        /**
         * Builds the path where a certain file with fileName should be stored
         * @param fileName is the name of the file to store
         * @return the path where the file should be stored
         */
        public final static String buildFileStoragePath(String fileName) {
            return FileSystem.FILES_DIRECTORY + "/" + fileName;
        }
    }


    /**
     * This class shouldn't be instantiated
     * or derived from. Ever.
     *
     * The exception guarantees that no one, not
     * even with reflection, can instantiate it.
     */
    private ServerConstants() {
        throw new UnsupportedOperationException();
    }
}
