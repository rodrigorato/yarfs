/**
 * Created by jorge at 04/12/17
 */
package a16.yarfs.client.service.file;

import a16.yarfs.client.ClientConstants;
import a16.yarfs.client.service.exception.AlreadyExecutedException;
import a16.yarfs.client.service.exception.NotExecutedException;
import a16.yarfs.client.service.exception.ServiceResultException;
import org.apache.commons.codec.binary.Base64;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;

/**
 *  Class CommitFileService
 *  This class is a service class to commit command.
 *  @see a16.yarfs.client.presentation.CommitCommand
 */
public class CommitFileService extends FileService{

    private String sessId;
    private String fileName;
    private byte[] fileContents;
    private byte[] fileSignature;
    private String fileId;

    public CommitFileService(String baseUrl, String sessId, String filename, byte[] contents,
                                byte[] signature, String fileId) throws MalformedURLException {
        super(baseUrl, ClientConstants.Endpoints.ADD_FILE);
        this.sessId = sessId;
        this.fileName = filename;
        this.fileContents = contents;
        this.fileSignature = signature;
        this.fileId = fileId;
    }

    @Override
    public void execute() throws IOException, AlreadyExecutedException {
        JSONObject req = new JSONObject();
        try {
            req.put("sessid", sessId);
            req.put("filename", fileName);

            req.put("signature", Base64.encodeBase64String(fileSignature));
            req.put("file", Base64.encodeBase64String(fileContents));
            req.put("file_id", fileId);

            setRequestParameters(req);

            super.execute();
            try {
                super.getResponse();
            } catch (NotExecutedException e) {
                e.printStackTrace();
            } catch (ServiceResultException e) {
                e.printStackTrace();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
