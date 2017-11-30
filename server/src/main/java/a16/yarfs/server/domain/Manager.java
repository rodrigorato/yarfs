/**
 * Created by jorge at 13/11/17
 */
package a16.yarfs.server.domain;

import a16.yarfs.server.domain.dto.ConcreteFileDto;
import a16.yarfs.server.domain.exceptions.AccessDeniedException;
import a16.yarfs.server.domain.exceptions.DuplicatedUsernameException;
import a16.yarfs.server.domain.exceptions.InvalidSessionException;
import a16.yarfs.server.domain.exceptions.WrongLoginException;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * Class Manager
 * jorge is an IDIOT because it hasn't made documentation for this class.
 */
public class Manager {

    private static Manager manager = null;
    private Map<String, User> users = new HashMap<String, User>();
    private Map<Long, Session> sessions = new HashMap<>();
    private static Logger logger = Logger.getLogger(Manager.class);
    private static long fileIdCounter = 0;
    private Map<User, List<Long>> userFiles = new HashMap<>();
    private final FileManager fileManager = new FileSystemFileManager();

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

    /**
     * Registers a new user in the system.
     * @param username username of the user to register.
     * @param password plain text password for the user.
     * @throws DuplicatedUsernameException happens if the given username already exists.
     */
    public void registerUser(String username, String password) throws DuplicatedUsernameException {
        if (users.containsKey(username)) {
            throw new DuplicatedUsernameException("Username " + username + " already exists");
        }
        User newuser = new User(username, password);
        users.put(username, newuser);
        userFiles.put(newuser, new ArrayList<>());
    }

    /**
     * Logs in a user into the system.
     * @param username username of the user to login.
     * @param password plain text password of the user to login.
     * @return token on successful login.
     * @throws WrongLoginException on wrong password/username
     */
    public String loginUser(String username, String password) throws WrongLoginException {
        logger.debug("Logging in user with username " + username);

        User user = users.get(username);
        if (user == null) {
            logger.warn("Can not login: user '" + username + "' does not exist");
            throw new WrongLoginException(username + " does not exist.");
        }
        try {
            Session session = new Session(this, user, password);
            long token = session.getToken();
            sessions.put(token, session);
            return Session.tokenToString(token);
        } catch (WrongLoginException e) {
            logger.warn("Can not login: wrong password for user '" + username + "'");
            throw new WrongLoginException("Wrong password for user " + username);
        }
    }

    public List<String> listUsers() { // FIXME:  Should this require a sessid?
        List<String> usernameList = new ArrayList<String>();
        usernameList.addAll(users.keySet());
        return usernameList;

    }

    /**
     * Adds a new file to the system.
     * @param filename filename of the file to add.
     * @param sessid session id of the user which wants to create a file.
     * @param fileContent contents of the file to add.
     * @param signature signed hash of the file.
     * @param cipheredKey cyphered symmetric key used to cypher the file
     *                    Kpub{K} being Kpub the public key of the owner and K the key in question.
     * @return id of the new file.
     * @throws InvalidSessionException session id is invalid or expired.
     */
    public long addFile(String filename, String sessid, byte[] fileContent,
                        byte[] signature, byte[] cipheredKey) throws  InvalidSessionException{
        if ( ! hasSession(sessid)){
            logger.warn("Invalid session " + sessid);
            throw new InvalidSessionException();
        }
        logger.debug("Creating a file " + filename);
        User owner = sessions.get(Session.stringToToken(sessid)).getUser();
        long fileId = Manager.getNextId();
        ConcreteFile cf = new ConcreteFile(fileId, owner.getUsername(), filename, fileContent, new Date(), signature);
        cf.addKey(owner.getUsername(), new SnapshotKey(cipheredKey));
        userFiles.get(owner).add(fileId);
        logger.info("Successfully added");
        logger.debug("User " + owner.getUsername() + " now has " + userFiles.get(owner).size() + " files.");
        logger.info("Witting file " + fileId + " to persistent storage");
        fileManager.writeFile(cf);
        return fileId;

    }

    /**
     * Method for getting files based on their ids.
     * @param sessid session id of the user which is trying to access.
     * @param fileId id of the file to be accessed.
     * @return file dto containing only the relevant data for the user.
     * @throws InvalidSessionException happens if the session id is invalid or expired.
     * @throws AccessDeniedException happens if the user doesn't have access to the requested file.
     */
    public ConcreteFileDto getFile(String sessid, String fileId) throws InvalidSessionException, AccessDeniedException {
        Session usersession = getSession(Session.stringToToken(sessid));
        logger.info("User "+usersession.getUser().getUsername()+" asking access to "+fileId);
        if( ! userFiles.get(usersession.getUser()).contains(Long.parseLong(fileId))){
            logger.info("Access denied of user "+usersession.getUser().getUsername()+" to file "+fileId);
            throw new AccessDeniedException();
        }
        logger.info("Access granted to user "+usersession.getUser().getUsername()+" on "+fileId);
        ConcreteFile file = (ConcreteFile) fileManager.readFile(Long.parseLong(fileId));
        return new ConcreteFileDto(file.getId(), file.getName(), file.getContent(), file.getCreationDate(),
                file.getSignature(), file.getOwnerId(), file.getKey(usersession.getUser().getUsername()));
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

    /**
     * Returns the id for the new file and increments the global counter.
     * @return id for the new file.
     */
    private static long getNextId(){
        return fileIdCounter++;
    }

    /**
     * Getter for the session object based on the id.
     * @param sessid id of the session.
     * @return session object with the sessionid described.
     * @throws InvalidSessionException happens if the session id is invalid or expired.
     */
    private Session getSession(long sessid) throws InvalidSessionException {
        if( ! sessions.keySet().contains(sessid)){
            throw new InvalidSessionException();
        }
        return sessions.get(sessid);
    }

}
