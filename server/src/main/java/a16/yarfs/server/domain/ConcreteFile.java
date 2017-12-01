/**
 * Created by jorge at 13/11/17
 */
package a16.yarfs.server.domain;

import java.util.Date;
import java.util.HashMap;

/**
 *  Class ConcreteFile
 *  Concrete implementation of the class File
 *  @see File
 */
public class ConcreteFile extends File{

    private HashMap<String,SnapshotKey> userKeys;

    /**
     * Constructor of all the parameters of the class
     *
     * @param id           Id for the file.
     * @param ownerId Owner of the file.
     * @param name         Name for the file.
     * @param content      Content of the file.
     * @param creationDate Creation date of the file.
     * @param signature    Cryptographic signature of the file.
     */
    public ConcreteFile(long id,String ownerId, String name, byte[] content, Date creationDate, byte[] signature) {
        super(id, ownerId, name, content, creationDate, signature);
        userKeys = new HashMap<>();
    }

    public ConcreteFile(byte[] content, FileMetadata metadata){
        super(content, metadata);
        userKeys = new HashMap<>();
    }

    /**
     * Getter for the key
     * @param username Username which key is to retrieve.
     * @return The snapshot key of the user.
     */
    public SnapshotKey getKey(String username){
        return userKeys.get(username);
    }

    /**
     * Adds a key to the given username.
     * @param username The username which the key will be added.
     * @param key The key to be added.
     */
    public void addKey(String username, SnapshotKey key){
        userKeys.put(username,key);
    }

    /**
     * Returns object as a readable string.
     * @return Object as a readable string.
     * @see File#toString()
     * @see SnapshotKey#toString()
     */
    public String toString(){
        return super.toString()+"\nkey: "+userKeys.toString();
    }

}
