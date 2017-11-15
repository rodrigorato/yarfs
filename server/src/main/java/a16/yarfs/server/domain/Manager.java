/**
 * Created by jorge at 13/11/17
 */
package a16.yarfs.server.domain;

import a16.yarfs.server.exception.DuplicatedUsernameException;

import java.util.HashMap;
import java.util.Map;

/**
 *  Class Manager
 *  jorge is an IDIOT because it hasn't made documentation for this class.
 */
public class Manager {

    private static Manager manager = null;
    private Map<String,User> users = new HashMap<String, User>();

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

}
