package a16.yarfs.ca.handlers.messages;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONException;

import java.security.Key;

/**
 * Created by Rodrigo Rato on 12/6/17.
 */
public class FinalMessage extends AbstractMessage {

    public FinalMessage(String json) throws JSONException {
        super(json);
    }

    public FinalMessage(byte[] message) throws JSONException {
        this.put("status", Base64.encodeBase64String(message));
    }

    public byte[] getMessage() throws JSONException {
        return Base64.decodeBase64(this.getString("status"));
    }
}
