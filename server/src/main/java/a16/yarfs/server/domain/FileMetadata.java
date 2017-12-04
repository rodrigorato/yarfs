/***
 * Created by Jorge Heleno
 */
package a16.yarfs.server.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

/**
 * Class used to have a division between file contents and attributes.
 * By using a class and not inside class File is because it is easier to
 * serialize/deserialize this way.
 */
public class FileMetadata implements Serializable{
    private long id;
    private String name;
    private Date creationDate;
    private byte[] signature;
    private String ownerId;
    private HashMap<String,SnapshotKey> userKeys;

    public FileMetadata(long id, String name, Date creationDate, byte[] signature, String ownerId){
        this.id = id;
        this.name = name;
        this.creationDate = creationDate;
        this.signature = signature;
        this.ownerId = ownerId;
        userKeys = new HashMap<>();

    }


    /**
     * Getter for the id.
     * @return The id of the file
     */
    public long getId() {
        return id;
    }

    /**
     * Getter for the name.
     * @return Name of the file.
     */
    public String getName() {
        return name;
    }
    public Date getCreationDate() {
        return creationDate;
    }

    /**
     * Getter for the cryptographic signature of the file.
     * @return Cryptographic signature of the file.
     */
    public byte[] getSignature() {
        return signature;
    }

    public String getOwnerId(){
        return ownerId;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setSignature(byte[] signature){
        this.signature = signature;
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

}
