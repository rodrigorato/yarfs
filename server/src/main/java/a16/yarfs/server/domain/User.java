/**
 * Created by nuno on 13/11/2017
 */
package a16.yarfs.server.domain;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.text.RandomStringGenerator;
import org.apache.log4j.Logger;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 *  Class User
 */
public class User {
    private static Logger logger = Logger.getLogger(User.class);

    /** unique username that identifies the User */
    private String username;

    private String salt;
    private byte[] passwordHash;

    /**
     * computes the hash of a given password using the user's own salt and username
     * @param password the password
     * @return salted hash of the password
     */
    protected byte[] getPasswordHash(String password) {
        String realSalt = username + salt + password;
        byte[] hash = null;
        try {
            MessageDigest md = MessageDigest.getInstance(ServerConstants.USER_PASSWD_DIGEST);
            try {
                hash = md.digest(realSalt.getBytes("UTF-8"));
            } catch (UnsupportedEncodingException e) {
                logger.warn("UTF-8 encoding not available lol", e);
                System.exit(-1);
            }
        } catch (NoSuchAlgorithmException e) {
            logger.error(ServerConstants.USER_PASSWD_DIGEST + " algorithm is not available.", e);
            System.exit(-1);
        }

        logger.debug("password hash: '" + new String(Hex.encodeHex(hash)) + "'");
        return hash;
    }

    /**
     * generates a random string
     * @return the newly generated random string
     */
    protected String generateRandomString() {
        // Generate 30 chars long string using '0'-'z' range (about 74 different values)
        RandomStringGenerator generator = new RandomStringGenerator.Builder()
                .withinRange('0', 'z')
                .build();
        String rand = generator.generate(30);
        logger.debug("random string: '"+rand+'"');
        return rand;
    }

    /** creates a new User
     * @param username that identifies the user
     * @param password its password
     */
    public User(String username, String password) {
        this.username = username;
        setPassword(password);
    }

    /** updates the user's password
     * @param password the new password for the user
     */
    public void setPassword(String password) {
        this.salt = generateRandomString();
        this.passwordHash = getPasswordHash(password);
    }

    /**
     * Authenticate this user using the given password
     * @param password to try
     * @returns true if the password matches the user's
     */
    public boolean authenticate(String password) {
        logger.debug("trying password '" + password+"'");
        return Arrays.equals(passwordHash, getPasswordHash(password));
    }

    /** get the user's username */
    public String getUsername() {
        return username;
    }
}
