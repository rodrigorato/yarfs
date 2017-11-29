/**
 * Created by jorge at 11/11/17
 **/
package a16.yarfs.client.service.user;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.net.MalformedURLException;
import java.security.PublicKey;

/**
 Class RegisterService

 **/
public class RegisterService extends AbstractUserService {
    private String username;
    private String password;
    private PublicKey key;

    protected RegisterService(String baseUrl) throws MalformedURLException {
        super(baseUrl, null);
        throw new NotImplementedException();
    }

    //FIXME
    public void execute() {
        throw new NotImplementedException();
    }
}
