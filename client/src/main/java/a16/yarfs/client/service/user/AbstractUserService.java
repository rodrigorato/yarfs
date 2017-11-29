/**
 * Created by jorge at 11/11/17
 **/
package a16.yarfs.client.service.user;

import a16.yarfs.client.service.AbstractHttpService;

import java.net.MalformedURLException;

/**
 Class AbstractUserService

 **/
public abstract class AbstractUserService extends AbstractHttpService {
    protected AbstractUserService(String baseUrl, String endpoint) throws MalformedURLException {
        super(baseUrl, endpoint);
    }

}
