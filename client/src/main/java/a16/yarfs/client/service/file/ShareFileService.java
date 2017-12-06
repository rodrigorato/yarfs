/**
 * Created by jorge on 05/12/17
 */
package a16.yarfs.client.service.file;

import a16.yarfs.client.ClientConstants;
import a16.yarfs.client.KeyManager;
import a16.yarfs.client.LocalFileManager;
import a16.yarfs.client.SecureLocalFileManager;
import a16.yarfs.client.service.dto.FileMetadata;
import a16.yarfs.client.service.exception.*;
import org.apache.commons.codec.binary.Base64;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.security.PublicKey;

/**
 * Class ShareFileService
 * jorge is an IDIOT because he didn't document this class.
 *
 */
public class ShareFileService extends FileService{

    private String filename;
    private String targetUser;
    private String sessid;

    public ShareFileService(String baseUrl, String filename, String targetUser, String sessid) throws MalformedURLException {
        super(baseUrl, ClientConstants.Endpoints.SHARE_FILE);
        this.filename = filename;
        this.targetUser = targetUser;
        this.sessid = sessid;
    }


    @Override
    public void execute() throws IOException, AlreadyExecutedException {

 /*       JSONObject req = new JSONObject();
//TODO
        try {
            req.put("target_user", targetUser);
            // Make request

        } catch (JSONException e) {
            e.printStackTrace();
        }*/
        JSONObject req = new JSONObject();


        byte[] targetKey = KeyManager.getTargetKey(targetUser);
        try {
//            (new RefreshService(ClientConstants.baseServerUrl, sessid)).execute();
            FileMetadata metadata = SecureLocalFileManager.getManager().getFileMetadata(this.filename);
            getLogger().debug("Acquired metadata for "+this.filename);
            byte[] cipheredKey =  KeyManager.AsymCipher(metadata.getKey(), targetKey); // ciphered with the public key of the target user
            req.put("user_key", Base64.encodeBase64String(cipheredKey));
            req.put("sessid", sessid);
            req.put("file_id", metadata.getId());
            req.put("target_user", targetUser);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            super.setRequestParameters(req);
            super.execute();
        } catch (ServiceExecutionException e) {
            e.printStackTrace();
        }


    }



    public boolean successful(){
        try {
            JSONObject response = super.getResponse();
            return response.getBoolean("success");
        } catch (IOException | NotExecutedException | ServiceResultException | JSONException e) {
            e.printStackTrace();
        }
        throw new RuntimeException();
    }

}
