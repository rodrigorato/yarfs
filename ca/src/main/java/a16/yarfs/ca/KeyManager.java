package a16.yarfs.ca;

import java.security.PublicKey;
import java.util.HashMap;

/**
 * Created by Rodrigo Rato on 12/6/17.
 */
public class KeyManager {
    private static KeyManager keymanager = null;

    private HashMap<String, PublicKey> usersPublicKeys;

    public static synchronized KeyManager getInstance() {
        if(keymanager == null) {
            keymanager = new KeyManager();
        }
        return keymanager;
    }

    private KeyManager() {
        usersPublicKeys = new HashMap<>();
    }

    public PublicKey getPublicKey(String username) {
        return usersPublicKeys.getOrDefault(username, null);
    }

    public boolean setPublicKey(String username, PublicKey pk) {
        if( ! usersPublicKeys.containsKey(username)) {
            usersPublicKeys.put(username, pk);
            return true;
        } else return false;
    }

}
