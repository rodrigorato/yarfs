/**
 * Created by nuno at 30/11/17
 */
package a16.yarfs.client.service.dto;

/**
 *  Class FileDto
 *  data type object to be used by the file services
 */
public class FileDto {



    private byte [] contents;
    private FileMetadata fileMetadata;


    public FileDto(long id, String name, String owner, byte[] contents, byte[] signature, byte[] key) {
        this.contents = contents;
        this.fileMetadata = new FileMetadata(id, name, owner, signature, key);
    }

    public FileDto(byte[] contents, FileMetadata fileMetadata){
        this.contents = contents;
        this.fileMetadata = fileMetadata;
    }

    public byte[] getContents() {
        return contents;
    }

    public byte[] getKey() {
        return fileMetadata.getKey();
    }

    public byte[] getSignature() {
        return fileMetadata.getSignature();
    }

    public long getId() {
        return fileMetadata.getId();
    }

    public String getName() {
        return fileMetadata.getName();
    }

    public String getOwner() {
        return fileMetadata.getOwner();
    }

    public FileMetadata getFileMetadata() {
        return fileMetadata;
    }
}
