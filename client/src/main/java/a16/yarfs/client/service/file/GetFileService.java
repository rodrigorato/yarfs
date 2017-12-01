/**
 * Created by jorge at 11/11/17
 **/
package a16.yarfs.client.service.file;

import a16.yarfs.client.ClientConstants;
import a16.yarfs.client.LocalFileManager;
import a16.yarfs.client.service.dto.FileDto;
import a16.yarfs.client.service.exception.AlreadyExecutedException;
import a16.yarfs.client.service.exception.NotExecutedException;
import a16.yarfs.client.service.exception.ServiceResultException;
import org.apache.commons.codec.binary.Base64;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;

/**
 * Class GetFileService
 **/
public class GetFileService extends FileService {

    private final String sessId;
    private final String fileName;

    public GetFileService(String baseUrl, String sessId, String fileName) throws MalformedURLException {
        super(baseUrl, ClientConstants.Endpoints.GET_FILE);
        this.sessId = sessId;
        this.fileName = fileName;
    }

    @Override
    public void execute() throws IOException, AlreadyExecutedException {
        JSONObject req = new JSONObject();
        try {

            req.put("sessid", sessId);

            try {
                req.put("fileid", String.valueOf(LocalFileManager.getManager().getFileMetadata(fileName).getId()));

                getLogger().debug("Sending request " + req);
                setRequestParameters(req);

                super.execute();
            } catch (IOException e) {
                getLogger().warn("File not found. What now?");
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public FileDto getFile() throws NotExecutedException, IOException, ServiceResultException {
        assertExecuted();

        try {
            JSONObject res = getResponse();

            long id = Long.parseLong(res.getString("fileid"));
            String filename = res.getString("filename");
            String owner = res.getString("owner");

            byte [] contents  = Base64.decodeBase64(res.getString("contents"));
            byte [] signature = Base64.decodeBase64(res.getString("signature"));
            byte [] key       = new byte[0];//Base64.decodeBase64(res.getString("key")); FIXME

            return new FileDto(id, filename, owner, contents, signature, key);
        } catch (JSONException e) {
            throw new ServiceResultException("response error: " + e.getMessage());
        }
    }
}
