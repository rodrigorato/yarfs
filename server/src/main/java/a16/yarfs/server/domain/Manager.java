/**
 * Created by jorge at 13/11/17
 */
package a16.yarfs.server.domain;

import a16.yarfs.server.domain.exceptions.DuplicatedUsernameException;
import a16.yarfs.server.domain.exceptions.WrongPasswordException;
import a16.yarfs.server.exception.api.LoginException;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class Manager
 * jorge is an IDIOT because it hasn't made documentation for this class.
 */
public class Manager {

    private static Manager manager = null;
    private Map<String, User> users = new HashMap<String, User>();
    private Map<Long, Session> sessions = new HashMap<>();
    private static Logger logger = Logger.getLogger(Manager.class);

    public static Manager getInstance() {
        if (manager == null) {
            manager = new Manager();
            return manager;
        }
        return manager;
    }

    /**
     * check if a session exists
     *
     * @param token the token that identifies the session
     * @returns true if a session exists with the given token
     */
    public boolean hasSession(long token) {
        Session s = sessions.get(token);
        if (s == null) {
            return false;
        }
        if (s.isExpired()) {
            sessions.remove(token);
            return false;
        }
        return true;
    }

    /**
     * check if a session exists
     *
     * @param token the token that identifies the session
     * @returns true if a session exists with the given token
     */
    public boolean hasSession(String token) {
        return hasSession(Session.stringToToken(token));
    }

    public void registerUser(String username, String password) {
        if (users.containsKey(username)) {
            throw new DuplicatedUsernameException("Username " + username + " already exists");
        }
        users.put(username, new User(username, password));
    }

    public String loginUser(String username, String password) throws LoginException {
        logger.debug("Logging in user with username " + username);

        User user = users.get(username);
        if (user == null) {
            logger.warn("Can not login: user '" + username + "' does not exist");
            throw new LoginException();
        }
        try {
            Session session = new Session(this, user, password);
            long token = session.getToken();
            sessions.put(token, session);
            return Session.tokenToString(token);
        } catch (WrongPasswordException e) {
            logger.warn("Can not login: wrong password for user '" + username + "'");
            throw new LoginException();
        }
    }

    public List<String> listUsers() { // FIXME:  Should this require a sessid?
        List<String> usernameList = new ArrayList<String>();
        usernameList.addAll(users.keySet());
        return usernameList;

    }

    /**
     * invalidates a Session
     *
     * @param token that identifies the Session
     */
    public void logout(long token) {
        sessions.remove(token);
    }

    /**
     * invalidates a Session
     *
     * @param token that identifies the Session
     */
    public void logout(String token) {
        logout(Session.stringToToken(token));
    }

    /**
     * Checks if a user exists.
     *
     * @param username username to check existence
     * @return if the user exists or no
     */
    public boolean hasUser(String username) {
        return users.containsKey(username);
    }

    /**
     * Destroys the manager static
     */
    public static void selfDestruct() {
        manager = null;
    }
}
