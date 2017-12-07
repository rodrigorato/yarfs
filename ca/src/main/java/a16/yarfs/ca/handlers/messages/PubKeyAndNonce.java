package a16.yarfs.ca.handlers.messages;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

/**
 * Created by Rodrigo Rato on 12/6/17.
 */
public class PubKeyAndNonce extends AbstractMessage {

    public PubKeyAndNonce(String json) throws JSONException {
        super(json);
    }

    public PubKeyAndNonce(long nonce, PublicKey pk) throws JSONException {
        this.put("nonce", nonce);
        this.put("client_public_key", Base64.encodeBase64String(pk.getEncoded()));
    }

    public long getNonce() throws JSONException {
        return this.getLong("nonce");
    }

    public PublicKey getClientPublicKey() throws JSONException, NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory f = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.decodeBase64(this.getString("client_public_key")));
        return f.generatePublic(keySpec);
    }
}
