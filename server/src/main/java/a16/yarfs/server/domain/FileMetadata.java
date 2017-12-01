/***
 * Created by Jorge Heleno
 */
package a16.yarfs.server.domain;

import java.io.Serializable;
import java.util.Date;

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

    public FileMetadata(long id, String name, Date creationDate, byte[] signature, String ownerId){
        this.id = id;
        this.name = name;
        this.creationDate = creationDate;
        this.signature = signature;
        this.ownerId = ownerId;

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


}
