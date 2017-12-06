/**
 * Created by jorge on 06/12/17
 **/
package a16.yarfs.client.service.file;

import a16.yarfs.client.ClientConstants;
import a16.yarfs.client.LocalFileManager;
import a16.yarfs.client.SecureLocalFileManager;
import a16.yarfs.client.service.AbstractHttpService;
import a16.yarfs.client.service.exception.AlreadyExecutedException;
import a16.yarfs.client.service.exception.NotExecutedException;
import a16.yarfs.client.service.exception.ServiceResultException;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;

/**
 * Class UnshareFileService
 * jorge is an IDIOT because he didn't document this class.
 *
 **/
public class UnshareFileService extends AbstractHttpService{
    private String sessid;
    private String filename;
    private String targetUser;
    public UnshareFileService(String baseUrl, String sessid, String filename, String targetUser)
            throws MalformedURLException {
        super(baseUrl, ClientConstants.Endpoints.UNSHARE_FILE);
        this.sessid = sessid;
        this.filename = filename;
        this.targetUser = targetUser;
    }


    @Override
    public void execute() throws IOException, AlreadyExecutedException {
        JSONObject request = new JSONObject();


        try {
            request.put("sessid", sessid);
            request.put("filename", String.valueOf(LocalFileManager.getManager().getFileMetadata(filename).getId()));
            request.put("target_user", targetUser);
            super.setRequestParameters(request);
            super.execute();


        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
    }

    public boolean getSuccess() throws NotExecutedException, IOException, ServiceResultException, JSONException {

        return super.getResponse().getBoolean("success");
    }
}
