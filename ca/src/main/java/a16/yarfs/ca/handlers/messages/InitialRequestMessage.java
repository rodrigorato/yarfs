package a16.yarfs.ca.handlers.messages;

import a16.yarfs.ca.CAConstants;
import org.json.JSONException;
import org.json.JSONObject;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import java.io.Serializable;
import java.security.*;

/**
 * Created by Rodrigo Rato on 12/6/17.
 */
public class InitialRequestMessage extends AbstractMessage  implements Serializable{

    public InitialRequestMessage(String json) throws JSONException { // FIXME this contructor has to exist
        super(json);
        System.out.println("Creating bla");
    }



    public InitialRequestMessage(byte[] cipheredKs, byte[] cipheredPubKeyAndNonce, byte[] hash) throws JSONException {
        this.put("m1", Base64.encodeBase64String(cipheredPubKeyAndNonce));
        this.put("k", Base64.encodeBase64String(cipheredKs));
        this.put("hash", Base64.encodeBase64String(hash));
    }

    public byte[] getCAPrivateCipheredKs() throws JSONException {
        System.out.println("Deciphering " + this.getString("k"));   // FIXME problem here, should get m1 then get k
        return Base64.decodeBase64(this.getString("k"));
    }

    public PubKeyAndNonce getPubKeyAndNonce(Key sessionKey) throws JSONException, GeneralSecurityException {
        return new PubKeyAndNonce(InitialRequestMessage.getJSONObjectFromCipheredB64(this.getString("m1"),
                sessionKey).toString());
    }

    public byte[] getHash() throws JSONException {
        return Base64.decodeBase64(this.getString("hash"));
    }
}
