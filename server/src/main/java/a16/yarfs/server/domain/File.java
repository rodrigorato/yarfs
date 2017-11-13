package a16.yarfs.server.domain;

import org.apache.log4j.Logger;

import java.io.Serializable;
import java.util.Date;


/**
 * Class File
 */
public abstract class File implements Serializable {

    private static Logger log = Logger.getLogger(File.class);
    private long id;
    private String name;
    private byte[] content;
    private Date creationDate;
    private byte[] signature;

    private void init(long id, String name, byte[] content, Date creationDate, byte[] signature){
        log.info("Starting new File");
        log.debug("File attributes are: ");
        this.id = id;
        this.name = name;
        this.content = content;
        this.creationDate = creationDate;
        this.signature = signature;
    }

    public File(long id, String name, byte [] content, Date creationDate, byte[] signature){
        init(id, name, content, creationDate, signature);
    }

    public String toString(){
        return "";
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
}

