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
public class TargetUserAndNonce extends AbstractMessage {


    public TargetUserAndNonce(String json) throws JSONException {
        super(json);
    }

    public TargetUserAndNonce(long nonce, String username) throws JSONException {
        this.put("nonce", nonce);
        this.put("target_user", username);
    }

    public long getNonce() throws JSONException {
        return this.getLong("nonce");
    }

    public String getTargetUserName() throws JSONException {
        return this.getString("target_user");
    }
}
