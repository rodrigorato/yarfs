/**
 * Created by jorge at 11/11/17
 **/
package a16.yarfs.client.service.file;

import a16.yarfs.client.ClientConstants;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.net.MalformedURLException;


/**
 Class DeleteFileService

 **/
public class DeleteFileService extends FileService {

    protected DeleteFileService(String baseUrl) throws MalformedURLException {
        super(baseUrl, ClientConstants.Endpoints.DELETE_FILE);
        throw new NotImplementedException();
    }

    /*FIXME*/
    public void execute() {
        throw new NotImplementedException();
    }
}
