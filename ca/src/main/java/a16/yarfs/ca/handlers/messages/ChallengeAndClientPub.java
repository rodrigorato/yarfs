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
public class ChallengeAndClientPub extends AbstractMessage {

    public ChallengeAndClientPub(long nonce1, long nonce2, byte[] cipheredChallenge, PublicKey clientPubKey) throws JSONException {
        this.put("nonce1", nonce1);
        this.put("nonce2", nonce2);
        this.put("ciphered_challenge", Base64.encodeBase64String(cipheredChallenge));
        this.put("client_public_key", Base64.encodeBase64String(clientPubKey.getEncoded()));
    }

    public long getNonce1() throws JSONException {
        return this.getLong("nonce1");
    }

    public long getNonce2() throws JSONException {
        return this.getLong("nonce2");
    }

    public byte[] getCipheredChallenge() throws JSONException {
        return Base64.decodeBase64(this.getString("ciphered_challenge"));
    }

    public PublicKey getClientPublicKey() throws JSONException, NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory f = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.decodeBase64(this.getString("client_public_key")));
        return f.generatePublic(keySpec);
    }
}
