/**
 * Created by jorge at 13/11/17
 */
package a16.yarfs.server.domain;

import java.io.Serializable;

/**
 *  Class SnapshotKey
 *  This class will hold a symmetric file used to cipher the file. This key is also ciphered with the owner public key.
 */
public class SnapshotKey implements Serializable{

    private byte[] cipheredKey;

    /**
     * Constructor for the class.
     * @param key The ciphered symmetric key.
     */
    public SnapshotKey(byte[] key){
        this.cipheredKey = key;
    }

    /**
     * Getter for the key.
     * @return Ciphered key with the owner's Public key.
     */
    public byte[] getCipheredKey() {
        return cipheredKey;
    }
}
