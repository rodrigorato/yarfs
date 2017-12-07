package a16.yarfs.ca.handlers.messages;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONException;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

/**
 * Created by Rodrigo Rato on 12/6/17.
 */
public class ChallengeResponse extends AbstractMessage {

    public ChallengeResponse(String json) throws JSONException {
        super(json);
    }

    public ChallengeResponse(long nonce2, long nonce3, byte[] challengeAnswer, String username, String sessionId) throws JSONException {
        this.put("nonce2", nonce2);
        this.put("nonce3", nonce3);
        this.put("challenge_answer", Base64.encodeBase64String(challengeAnswer));
        this.put("username", username);
        this.put("sessionid", sessionId);
    }

    public long getNonce2() throws JSONException {
        return this.getLong("nonce2");
    }

    public long getNonce3() throws JSONException {
        return this.getLong("nonce3");
    }

    public byte[] getChallengeAnswer() throws JSONException {
        return Base64.decodeBase64(this.getString("challenge_answer"));
    }

    public String getUsername() throws JSONException {
        return this.getString("username");
    }

    public String getSessionId() throws JSONException {
        return this.getString("sessionid");
    }
}
