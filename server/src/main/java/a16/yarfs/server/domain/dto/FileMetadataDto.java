/***
 * Created by Jorge Heleno
 */

package a16.yarfs.server.domain.dto;

import a16.yarfs.server.domain.SnapshotKey;

import java.util.Date;

/***
 * This class will be used as dto for the abstraction of file metadata.
 */
public class FileMetadataDto {

    private long id;
    private String name;
    private Date creationDate;
    private byte[] signature;
    private String ownerId;
    private String lastModifiedBy;
    private byte[] userKey;

    public FileMetadataDto(long id, String name, Date creationDate, byte[] signature, String ownerId,
                           String lastModifiedBy, byte[] userKey){
        this.id = id;
        this.name = name;
        this.creationDate = creationDate;
        this.signature = signature;
        this.ownerId = ownerId;
        this.lastModifiedBy = lastModifiedBy;
        this.userKey =  userKey;

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

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public byte[] getUserKey() {
        return userKey;
    }
}
