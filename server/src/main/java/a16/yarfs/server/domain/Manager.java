/**
 * Created by jorge at 13/11/17
 */
package a16.yarfs.server.domain;

import a16.yarfs.server.exception.DuplicatedUsernameException;
import a16.yarfs.server.exception.api.LoginException;
import org.apache.log4j.Logger;

import java.util.*;

/**
 *  Class Manager
 *  jorge is an IDIOT because it hasn't made documentation for this class.
 */
public class Manager {

    private static Manager manager = null;
    private Map<String,User> users = new HashMap<String, User>();
    private static Logger logger = Logger.getLogger(Manager.class);

    public static Manager getInstance() {
        if (manager == null) {
            manager = new Manager();
            return manager;
        }
        return manager;
    }

    public void registerUser(String username, String password){
        if (users.containsKey(username)){
            throw new DuplicatedUsernameException("Username "+username+" already exists");
        }
        users.put(username, new User(username,password));
    }

    public String loginUser(String username, String hashpass){
        logger.debug("Logging in user with username " + username);
        try {
            if (!users.get(username).authenticate(hashpass)) {
                logger.warn("Error logging in username " + username);
                throw new LoginException();
            }
        } catch( RuntimeException e){
            logger.warn("Something bad happened?");
            throw new LoginException();
        }
        return UUID.randomUUID().toString();
    }

    public List<String> listUsers(){ // FIXME:  Should this require a sessid?
        List<String> usernameList = new ArrayList<String>();
        usernameList.addAll(users.keySet());
        return usernameList;

    }

}
