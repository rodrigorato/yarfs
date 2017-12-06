package a16.yarfs.ca.handlers.messages;

import a16.yarfs.ca.CAConstants;
import org.apache.commons.codec.binary.Base64;
import org.json.JSONException;
import org.json.JSONObject;

import javax.crypto.Cipher;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.PublicKey;

/**
 * Created by Rodrigo Rato on 12/6/17.
 */
public class RequestChallengeMessage extends AbstractMessage {


    public RequestChallengeMessage(byte[] cipheredChallengeAndClientPub, byte[] cipheredHash) throws JSONException {
        this.put("m2", Base64.encodeBase64String(cipheredChallengeAndClientPub));
        this.put("ciphered_hash", Base64.encodeBase64String(cipheredHash));
    }

    public ChallengeAndClientPub getChallengeAndClientPub(Key sessionKey) throws JSONException, GeneralSecurityException {
        return (ChallengeAndClientPub) AbstractMessage
                .getJSONObjectFromCipheredB64(this.getString("m2"), sessionKey);
    }

    public byte[] getCipheredHash() throws JSONException {
        return Base64.decodeBase64(this.getString("ciphered_hash"));
    }

    public byte[] getUncipheredHash(PublicKey pubKey) throws JSONException, GeneralSecurityException {
        byte[] cipheredHash = getCipheredHash();
        return AbstractMessage.decipherBytesWithPublicKey(cipheredHash, pubKey);
    }

}
