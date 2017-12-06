package a16.yarfs.ca.handlers.messages;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONException;

import java.security.GeneralSecurityException;
import java.security.Key;

/**
 * Created by Rodrigo Rato on 12/6/17.
 */
public class ChallengeResponseMessage extends AbstractMessage {

    public ChallengeResponseMessage(byte[] cipheredChallengeAndAuthentication, byte[] hash) throws JSONException {
        this.put("m3", Base64.encodeBase64String(cipheredChallengeAndAuthentication));
        this.put("hash", Base64.encodeBase64String(hash));
    }

    public ChallengeResponse getChallengeResponse(Key sessionKey) throws JSONException, GeneralSecurityException {
        return (ChallengeResponse) AbstractMessage
                .getJSONObjectFromCipheredB64(this.getString("m3"), sessionKey);
    }

    public byte[] getHash() throws JSONException {
        return Base64.decodeBase64(this.getString("hash"));
    }

}
