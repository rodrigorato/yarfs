/**
 * Created by jorge at 13/11/17
 */
package a16.yarfs.server.domain;

import a16.yarfs.server.domain.dto.ConcreteFileDto;
import a16.yarfs.server.domain.dto.FileMetadataDto;
import a16.yarfs.server.domain.exceptions.AccessDeniedException;
import a16.yarfs.server.domain.exceptions.DuplicatedUsernameException;
import a16.yarfs.server.domain.exceptions.InvalidSessionException;
import a16.yarfs.server.domain.exceptions.WrongLoginException;
import a16.yarfs.server.exception.http.InternalServerErrorException;
import org.apache.log4j.Logger;

import java.io.IOException;
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
        logger.debug("Adding key for " + owner.getUsername() + "\nKey is "+ Arrays.toString(cipheredKey));
        cf.addKey(owner.getUsername(), new SnapshotKey(cipheredKey));
        cf.getKey(owner.getUsername());
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
        logger.debug("User key is " + file.getKey(usersession.getUser().getUsername()));
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

    /**
     * Method used to get a metadata of a single file.
     * @param sessid session id of the user trying to access.
     * @param fileId file id of the file the user is trying to access.
     * @return FileMetadataDto of the file with requested fileId
     * @see FileManager#readFileMetadata(String)
     */
    public FileMetadataDto getFileMetadata(String sessid, Long fileId){
        if( ! hasSession(sessid)){ // basic check of valid session id
            logger.warn("Unauthorized access attempt to file " + fileId);
            throw new AccessDeniedException();
        }
        try {
            User requester = sessions.get(Session.stringToToken(sessid)).getUser();
            if (userFiles.containsKey(requester)) {
                FileMetadata fm = fileManager.readFileMetadata(String.valueOf(fileId)); //read the file metadata
                return new FileMetadataDto(fm.getId(), fm.getName(), fm.getCreationDate(),
                        fm.getSignature(), fm.getOwnerId());
            }
            else{
                throw new AccessDeniedException();
            }

            // From here on out just exceptions....java stuff
        } catch (IOException e) {
            logger.error("Some bad IO exception happened", e);
            throw new InternalServerErrorException();
        } catch (ClassNotFoundException e) {
            logger.error("Class not found? Maybe error in file format?", e);
            throw new InternalServerErrorException();
        }
    }

    /**
     * Method user to get the metadata of the files for all files of the user.
     * @param sessid session id of the user requesting access.
     * @return List of FileMetadataDto of all the files of the user with session sessid.
     */
    public List<FileMetadataDto> getFilesMetadata(String sessid){
        if( ! hasSession(sessid)){ // basic validation for session id
            throw new InvalidSessionException();
        }
        List<FileMetadataDto> fileMetadataList = new ArrayList<>();
        for (Long fileid : userFiles.get(sessions.get(Session.stringToToken(sessid)).getUser())){ // For all fileIds belonging to sessid
            logger.debug("Adding file read on " + fileid);
            fileMetadataList.add(getFileMetadata(sessid, fileid));
        }
        return fileMetadataList;
    }

}
