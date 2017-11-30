/**
 * Created by nuno at 30/11/17
 */
package a16.yarfs.client.service.dto;

/**
 *  Class FileDto
 *  data type object to be used by the file services
 */
public class FileDto {
    private long id;
    private String name;
    private String owner;

    private byte [] contents;
    private byte [] signature;
    private byte [] key;


    public FileDto(long id, String name, String owner, byte[] contents, byte[] signature, byte[] key) {
        this.id = id;
        this.name = name;
        this.owner = owner;
        this.contents = contents;
        this.signature = signature;
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public String getOwner() {
        return owner;
    }

    public byte[] getContents() {
        return contents;
    }

    public byte[] getKey() {
        return key;
    }

    public byte[] getSignature() {
        return signature;
    }

    public long getId() {
        return id;
    }
}
