package a16.yarfs.ca.handlers.messages;

import a16.yarfs.ca.CAConstants;
import org.apache.commons.codec.binary.Base64;
import org.json.JSONException;
import org.json.JSONObject;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import java.io.Serializable;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.PublicKey;

/**
 * Created by Rodrigo Rato on 12/6/17.
 */
public abstract class AbstractMessage extends JSONObject  implements Serializable{

    public AbstractMessage(String json) throws JSONException {
        super(json);
    }

    protected AbstractMessage(){

    }

    public static byte[] cipherJSONObjectWithSymKey(JSONObject that, Key ks) throws GeneralSecurityException {
        Cipher c = Cipher.getInstance(CAConstants.PublishService.SYMMETRIC_CIPHER_ALGORITHM);
        c.init(Cipher.ENCRYPT_MODE, ks);

        return c.doFinal(that.toString().getBytes());
    }

    public static byte[] cipherDataWithSymKey(byte[] data, SecretKey ks) throws GeneralSecurityException {
        Cipher c = Cipher.getInstance(CAConstants.PublishService.SYMMETRIC_CIPHER_ALGORITHM);
        c.init(Cipher.ENCRYPT_MODE, ks);

        return c.doFinal(data);
    }

    public static Object getJSONObjectFromCipheredB64(String b64CipheredJSONObject, Key ks) throws GeneralSecurityException, JSONException {
        Cipher c = Cipher.getInstance(CAConstants.PublishService.SYMMETRIC_CIPHER_ALGORITHM);
        c.init(Cipher.DECRYPT_MODE, ks);

        byte[] decryptedData = c.doFinal(Base64.decodeBase64(b64CipheredJSONObject));

        return new JSONObject(new String(decryptedData));
    }

    public static byte[] decipherBytesWithPublicKey(byte[] data, PublicKey pk) throws GeneralSecurityException {
        Cipher c = Cipher.getInstance(CAConstants.Keys.CA_KEYSTORE_CIPHER);
        c.init(Cipher.DECRYPT_MODE, pk);

        return c.doFinal(data);
    }

}
