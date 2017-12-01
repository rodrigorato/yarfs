package a16.yarfs.client.service.dto;


import java.io.Serializable;

public class FileMetadata implements Serializable {
    private long id;
    private String name;
    private String owner;
    private byte [] signature;
    private byte [] key;

    public FileMetadata(long id, String name, String owner, byte[] signature, byte[] key){
        this.id = id;
        this.name = name;
        this.owner = owner;
        this.signature = signature;
        this.key = key;
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

    public String getName() {
        return name;
    }

    public String getOwner() {
        return owner;
    }

}
