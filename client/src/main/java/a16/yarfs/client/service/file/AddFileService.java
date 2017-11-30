/**
 * Created by jorge at 11/11/17
 **/
package a16.yarfs.client.service.file;

import a16.yarfs.client.ClientConstants;
import a16.yarfs.client.service.exception.AlreadyExecutedException;
import a16.yarfs.client.service.exception.NotExecutedException;
import a16.yarfs.client.service.exception.ServiceResultException;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;

import org.apache.commons.codec.binary.Base64;
/**
 Class AddFileService

 **/
public class AddFileService extends FileService {

    private final String sessId;
    private final String fileName;
    private final byte[] fileContents;
    private final byte[] fileSignature;
    private final byte[] fileKey;

    public AddFileService(String baseUrl, String sessId, String filename, byte[] contents, byte[] signature, byte[] key) throws MalformedURLException {
        super(baseUrl, ClientConstants.Endpoints.ADD_FILE);
        this.sessId = sessId;
        fileName = filename;
        fileContents = contents;
        fileSignature = signature;
        fileKey = key;
    }

    @Override
    public void execute() throws IOException, AlreadyExecutedException {
        JSONObject req = new JSONObject();
        try {
            req.put("sessid", sessId);
            req.put("filename", fileName);

            req.put("signature", Base64.encodeBase64String(fileSignature));
            req.put("key", Base64.encodeBase64String(fileKey));
            req.put("file", Base64.encodeBase64String(fileContents));

            setRequestParameters(req);

            super.execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * get the result of the service
     * @return the file id of the newly added file
     * @throws NotExecutedException
     * @throws IOException
     * @throws ServiceResultException if the file was not added
     */
    public long getFileId() throws NotExecutedException, IOException, ServiceResultException {
        assertExecuted();

        try {
            JSONObject res = getResponse();
            return Long.parseLong(res.getString("id"));
        } catch (JSONException e) {
            e.printStackTrace();
            throw new ServiceResultException("no file id included in the response");
        }
    }
}
