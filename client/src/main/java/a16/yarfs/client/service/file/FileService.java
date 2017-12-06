/**
 * Created by jorge at 11/11/17
 **/
package a16.yarfs.client.service.file;

import a16.yarfs.client.service.AbstractHttpService;

import java.net.MalformedURLException;
import java.security.PrivateKey;

/**
 Class FileService

 **/
public abstract class FileService extends AbstractHttpService {

    protected FileService(String baseUrl, String endpoint) throws MalformedURLException {
        super(baseUrl, endpoint);
    }


}
