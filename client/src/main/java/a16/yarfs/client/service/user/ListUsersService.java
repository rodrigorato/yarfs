/**
 * Created by jorge at 11/11/17
 **/
package a16.yarfs.client.service.user;

import a16.yarfs.client.ClientConstants;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.net.MalformedURLException;


/**
 Class ListUsersService

 **/
public class ListUsersService extends AbstractUserService {


    protected ListUsersService(String baseUrl) throws MalformedURLException {
        super(baseUrl, ClientConstants.Endpoints.LIST_USERS);
        throw new NotImplementedException();
    }

    //FIXME
    public void execute() {
        throw new NotImplementedException();
    }
}
