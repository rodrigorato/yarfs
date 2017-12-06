package a16.yarfs.ca.handlers;

import a16.yarfs.ca.CAConstants;

import javax.crypto.Cipher;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;

import a16.yarfs.ca.handlers.exceptions.CipherException;
import a16.yarfs.ca.handlers.exceptions.DecipherException;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;
import org.apache.commons.codec.binary.Base64;

/**
 * Created by Rodrigo Rato on 12/6/17.
 */
public abstract class AbstractTcpHandler {

    private static final Logger logger = Logger.getLogger(AbstractTcpHandler.class);

    public abstract void handle();

    public static byte[] decipherWithPrivateKey(byte[] cipheredData, PrivateKey key) throws DecipherException {
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance(CAConstants.Keys.CA_KEYSTORE_CIPHER);
            cipher.init(Cipher.DECRYPT_MODE, key);
            return cipher.doFinal(cipheredData);

        } catch (GeneralSecurityException e) {
            logger.error("Error deciphering data with PrivateKey: " + key.hashCode() + "; " + e.getMessage());
            throw new DecipherException("Error deciphering data with PrivateKey: " + key.hashCode() + "; " + e.getMessage());
        }
    }

    public static byte[] cipherWithKey(byte[] data, Key key) throws CipherException {
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance(CAConstants.Keys.CA_KEYSTORE_CIPHER);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return cipher.doFinal(data);

        } catch (GeneralSecurityException e) {
            logger.error("Error Ciphering data with Key: " + key.hashCode() + "; " + e.getMessage());
            throw new CipherException("Error Ciphering data with Key: " + key.hashCode() + "; " + e.getMessage());
        }
    }

    public static byte[] getByteArrayFromBase64String(String b64) {
        return Base64.decodeBase64(b64);
    }

    public static String getBase64StringFromByteArray(byte[] data) {
        return Base64.encodeBase64String(data);
    }

    public static byte[] sha256Digest(byte[] message) {
        return DigestUtils.sha256(message);
    }

    public static String getSha256InBase64(byte[] data){
        return getBase64StringFromByteArray(sha256Digest(data));
    }

}
