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
    private long id;
    private String name;
    private byte[] content;
    private Date creationDate;
    private byte[] signature;
    private long ownerId;

    /**
     * General method for objection initialization in this class.
     * @param id id for the file.
     * @param ownerId Owner of the file.
     * @param name name for the file.
     * @param content content of the file.
     * @param creationDate creation date of the file.
     * @param signature cryptographic signature of the file.
     */
    protected void init(long id, long ownerId, String name, byte[] content, Date creationDate, byte[] signature){
        log.info("Starting new File");
        this.id = id;
        this.name = name;
        this.content = content;
        this.creationDate = creationDate;
        this.signature = signature;
        this.ownerId = ownerId;
        log.debug("File attributes are: \n"+this.toString());
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
    public File(long id,long ownerId, String name, byte [] content, Date creationDate, byte[] signature){
        init(id, ownerId, name, content, creationDate, signature);
    }

    /**
     * Returns object as a readable string.
     * @see Object#toString()
     * @return Instance as a string.
     */
    public String toString(){
        return "id: "+this.id+"\nowner id: "+this.ownerId+"\nname: "+this.name+"\ncontent: "+this.content.toString()+"\ncreation date: "+
                creationDate.toString()+"signature: "+this.signature.toString();
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
        return creationDate;
    }

    /**
     * Getter for the cryptographic signature of the file.
     * @return Cryptographic signature of the file.
     */
    public byte[] getSignature() {
        return signature;
    }
}

