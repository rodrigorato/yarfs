package a16.yarfs.client.service.file;

import a16.yarfs.client.ClientConstants;
import a16.yarfs.client.LocalFileManager;
import a16.yarfs.client.service.dto.FileMetadata;
import a16.yarfs.client.service.exception.AlreadyExecutedException;
import a16.yarfs.client.service.exception.NotExecutedException;
import a16.yarfs.client.service.exception.ServiceExecutionException;
import a16.yarfs.client.service.exception.ServiceResultException;
import org.apache.commons.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

public class RefreshService extends FileService {

    private String sessid;
    private List<String> newFiles = new ArrayList<>();

    public RefreshService(String baseUrl, String sessid) throws MalformedURLException {
        super(baseUrl, ClientConstants.Endpoints.REFRESH);
        this.sessid = sessid;
    }

    @Override
    public void execute() throws IOException, AlreadyExecutedException, ServiceExecutionException {
        JSONObject request = new JSONObject();
        try {
            request.put("sessid", sessid);
            getLogger().debug("Sending request " + request);
            super.setRequestParameters(request);
            super.execute();
            JSONObject response = getResponse();
            JSONArray listOfMetadata = response.getJSONArray("files");
            List<FileMetadata> files = new ArrayList<>();
            for (int i = 0; i < listOfMetadata.length(); i++) {
                JSONObject innerObject = listOfMetadata.getJSONObject(i);
                files.add(new FileMetadata(Long.parseLong(innerObject.getString("file_id")),
                        innerObject.getString("file_name"),
                        innerObject.getString("owner_id"),
                        Base64.decodeBase64(innerObject.getString("signature")),
                        Base64.decodeBase64(innerObject.getString("key")),
                        innerObject.getString("last_modified_by"))); // FIXME user key not being sent. Should it?
            }
            for (FileMetadata fileMetadata : files) {
                try{
                    LocalFileManager.getManager().getFileMetadata(fileMetadata.getName());
                }catch (IOException e){
                    newFiles.add(fileMetadata.getName());
                }
                LocalFileManager.getManager().putFileMetaData(fileMetadata.getName(), fileMetadata);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (NotExecutedException e) {
            e.printStackTrace();
        } catch (ServiceResultException e) {
            e.printStackTrace();
        }

    }


    public List<String> getNewFiles(){
        return newFiles;
    }

}

