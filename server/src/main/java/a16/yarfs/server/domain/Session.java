/**
 * Created by nuno at 18/11/17
 */
package a16.yarfs.server.domain;

import a16.yarfs.server.ServerConstants;
import a16.yarfs.server.domain.exceptions.WrongLoginException;
import org.apache.log4j.Logger;

import java.io.Serializable;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Random;

/**
 * Class Session
 * Stores information about an existing session on the server.
 */
public class Session implements Serializable {
    private static Logger logger = Logger.getLogger(Session.class);
    private static final long serialVersionUID = 20171118230155L;

    /**
     * token that identifies the session
     */
    private long token;

    /**
     * date after which the session is no longer valid
     * Note: LocalDateTime is easier to manipulate than Date
     */
    private LocalDateTime expirationDate;

    /**
     * user that created the session
     */
    private User owner;


    /**
     * create a new session for the given user if properly authenticated
     *
     * @param manager  The Manager that is creating the session
     * @param u        The owner of the session
     * @param password used to authenticate the User
     */
    public Session(Manager manager, User u, String password) throws WrongLoginException {
        if (!u.authenticate(password)) {
            throw new WrongLoginException("password does not match");
        }
        owner = u;
        token = generateToken(manager);
        expirationDate = LocalDateTime.now().plusHours(ServerConstants.SESSION_DURATION_HOURS);
        logger.debug("new Session for '" + u.getUsername() + "' using new token "
                + Session.tokenToString(token) + " expires at " + expirationDate.toString());
    }

    /**
     * generates a new token that is unique for the given Manager
     *
     * @param manager the manager that is creating the session
     * @return the new random token
     */
    private long generateToken(Manager manager) {
        long token;
        do {
            token = (new BigInteger(64, new Random())).longValue();
        } while (token == 0 || manager.hasSession(token));
        return token;
    }

    /**
     * Check if the session has expired and should no longer be used
     *
     * @returns true if the session is expired
     */
    public boolean isExpired() {
        return expirationDate.isBefore(LocalDateTime.now());
    }

    public static String tokenToString(long token) {
        return Long.toString(token, 16);
    }

    public static long stringToToken(String token) {
        return Long.parseLong(token, 16);
    }

    /**
     * get the token that identifies this session
     */
    public long getToken() {
        return token;
    }

    /**
     * get the user that owns this session
     */
    public User getUser() {
        return owner;
    }
}
