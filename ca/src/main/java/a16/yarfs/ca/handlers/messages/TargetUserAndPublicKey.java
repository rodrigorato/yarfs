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
public class TargetUserAndPublicKey extends AbstractMessage {

    public TargetUserAndPublicKey(long nonce, String targetUsername, PublicKey targetPk) throws JSONException {
        this.put("nonce", nonce);
        this.put("target_username", targetUsername);
        this.put("target_public_key", Base64.encodeBase64String(targetPk.getEncoded()));
    }

    public long getNonce() throws JSONException {
        return this.getLong("nonce");
    }

    public String getTargetUsername() throws JSONException {
        return this.getString("target_username");
    }

    public PublicKey getTargetUserPublicKey() throws JSONException, NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory f = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.decodeBase64(this.getString("target_public_key")));
        return f.generatePublic(keySpec);
    }
}
