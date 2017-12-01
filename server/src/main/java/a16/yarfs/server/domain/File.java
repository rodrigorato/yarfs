package a16.yarfs.server.domain;

import org.apache.log4j.Logger;

import java.io.Serializable;
import java.util.Date;


/**
 * Class File
 * Abstract class which takes care of File general functions and attributes.
 */
public abstract class File implements Serializable {

    private static Logger log = Logger.getLogger(File.class);
    private FileMetadata fileMetadata;
    private byte[] content;

    /**
     * General method for objection initialization in this class.
     * @param id id for the file.
     * @param ownerId Owner of the file.
     * @param name name for the file.
     * @param content content of the file.
     * @param creationDate creation date of the file.
     * @param signature cryptographic signature of the file.
     */
    protected void init(long id, String ownerId, String name, byte[] content, Date creationDate, byte[] signature){
        log.info("Starting new File");
        this.content = content;
        fileMetadata = new FileMetadata(id, name, creationDate, signature, ownerId);
        log.info("File successfully created!");
    }

    /**
     * Constructor of all the parameters of the class
     * @param id Id for the file.
     * @param ownerId Owner of the file.
     * @param name Name for the file.
     * @param content Content of the file.
     * @param creationDate Creation date of the file.
     * @param signature Cryptographic signature of the file.
     */
    public File(long id,String ownerId, String name, byte [] content, Date creationDate, byte[] signature){
        init(id, ownerId, name, content, creationDate, signature);
    }

    public File(byte[] content, FileMetadata metadata){
        this.content = content;
        this.fileMetadata = metadata;
    }

    /**
     * Returns object as a readable string.
     * @see Object#toString()
     * @return Instance as a string.
     */
    @Deprecated
    public String toString(){
        //return "id: "+this.id+"\nowner id: "+this.ownerId+"\nname: "+this.name+"\ncontent: "+this.content.toString()+"\ncreation date: "+
//                creationDate.toString()+"signature: "+this.signature.toString();
        return "Content: " + content.toString();
    }

    /**
     * Getter for the id.
     * @return The id of the file
     */
    public long getId() {
        return fileMetadata.getId();
    }

    /**
     * Getter for the name.
     * @return Name of the file.
     */
    public String getName() {
        return fileMetadata.getName();
    }

    /**
     * Getter for the content.
     * @return Content of the file. Might be non-readable characters.
     */
    public byte[] getContent() {
        return content;
    }

    /**
     * Getter for the creation date.
     * @return Creation date of the file.
     */
    public Date getCreationDate() {
        return fileMetadata.getCreationDate();
    }

    /**
     * Getter for the cryptographic signature of the file.
     * @return Cryptographic signature of the file.
     */
    public byte[] getSignature() {
        return fileMetadata.getSignature();
    }

    public String getOwnerId(){
        return fileMetadata.getOwnerId();
    }

    public FileMetadata getFileMetadata() {
        return fileMetadata;
    }

    public void setName(String name){
        fileMetadata.setName(name);
    }

    public void setContent(byte[] content){
        this.content = content;
    }

    public void setSignature(byte[] signature){
        fileMetadata.setSignature(signature);
    }
}

