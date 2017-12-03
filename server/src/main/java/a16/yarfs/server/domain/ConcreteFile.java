/**
 * Created by jorge at 13/11/17
 */
package a16.yarfs.server.domain;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 *  Class ConcreteFile
 *  Concrete implementation of the class File
 *  @see File
 */
public class ConcreteFile extends File{


    /**
     * Constructor of all the parameters of the class
     *
     * @param id           Id for the file.
     * @param ownerId Owner of the file.
     * @param name         Name for the file.
     * @param content      Content of the file.
     * @param creationDate Creation date of the file.
     * @param signature    Cryptographic signature of the file.
     */
    public ConcreteFile(long id,String ownerId, String name, byte[] content, Date creationDate, byte[] signature) {
        super(id, ownerId, name, content, creationDate, signature);
    }

    public ConcreteFile(byte[] content, FileMetadata metadata){
        super(content, metadata);
    }

}
