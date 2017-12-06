/**
 * Created by Jorge Heleno
 */
package a16.yarfs.server.domain.dto;

import a16.yarfs.server.domain.SnapshotKey;

import java.util.Date;

/**
 * Class ConcreteFileDto
 *          This class serves as a Dto to concrete file
 *          @see a16.yarfs.server.domain.ConcreteFile
 */
public class ConcreteFileDto {

    private long id;
    private String name;
    private byte[] content;
    private Date creationDate;
    private byte[] signature;
    private String ownerId;
    private SnapshotKey userKey;
    private String lastModifiedBy;

    public ConcreteFileDto(long id, String name, byte[] content, Date creationDate, byte[] signature,
                           String ownerId, SnapshotKey userKey, String lastModifiedBy){
        this.id = id;
        this.name = name;
        this.content = content;
        this.creationDate = creationDate;
        this.signature = signature;
        this.ownerId = ownerId;
        this.userKey = userKey;
        this.lastModifiedBy = lastModifiedBy;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public byte[] getContent() {
        return content;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public byte[] getSignature() {
        return signature;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public SnapshotKey getUserKey() {
        return userKey;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }
}
