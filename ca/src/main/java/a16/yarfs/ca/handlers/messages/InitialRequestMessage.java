package a16.yarfs.ca.handlers.messages;

import a16.yarfs.ca.CAConstants;
import org.json.JSONException;
import org.json.JSONObject;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import java.security.*;

/**
 * Created by Rodrigo Rato on 12/6/17.
 */
public class InitialRequestMessage extends AbstractMessage {


    public InitialRequestMessage(byte[] cipheredKs, byte[] cipheredPubKeyAndNonce, byte[] hash) throws JSONException {
        this.put("k", Base64.encodeBase64(cipheredKs));
        this.put("m1", Base64.encodeBase64String(cipheredPubKeyAndNonce));
        this.put("hash", Base64.encodeBase64String(hash));
    }

    public byte[] getCAPrivateCipheredKs() throws JSONException {
        return Base64.decodeBase64(this.getString("k"));
    }

    public PubKeyAndNonce getPubKeyAndNonce(Key sessionKey) throws JSONException, GeneralSecurityException {
        return (PubKeyAndNonce) InitialRequestMessage.getJSONObjectFromCipheredB64(this.getString("m1"), sessionKey);
    }

    public byte[] getHash() throws JSONException {
        return Base64.decodeBase64(this.getString("hash"));
    }
}
