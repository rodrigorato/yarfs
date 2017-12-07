package a16.yarfs.ca.handlers.messages;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONException;

import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.PublicKey;

/**
 * Created by Rodrigo Rato on 12/6/17.
 */
public class TargetPubKeyMessage extends AbstractMessage {

    public TargetPubKeyMessage(String json) throws JSONException {
        super(json);
    }


    public TargetPubKeyMessage(byte[] cipheredTargetUserAndPublicKey, byte[] caCipheredHash) throws JSONException {
        this.put("m5", Base64.encodeBase64String(cipheredTargetUserAndPublicKey));
        this.put("ca_ciphered_hash", Base64.encodeBase64String(caCipheredHash));
    }

    public TargetUserAndPublicKey getTargetUserAndPublicKey(Key sessionKey) throws JSONException, GeneralSecurityException {
        return new TargetUserAndPublicKey(AbstractMessage
                .getJSONObjectFromCipheredB64(this.getString("m5"), sessionKey).toString());
    }

    public byte[] getCipheredHash() throws JSONException {
        return Base64.decodeBase64(this.getString("ca_ciphered_hash"));
    }

    public byte[] getUncipheredHash(PublicKey pubKey) throws JSONException, GeneralSecurityException {
        byte[] cipheredHash = getCipheredHash();
        return AbstractMessage.decipherBytesWithPublicKey(cipheredHash, pubKey);
    }

}
