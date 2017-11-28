/**
 * Created by jorge at 11/11/17
 **/
package a16.yarfs.client.command.file;

import a16.yarfs.client.ClientConstants;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.net.MalformedURLException;


/**
 Class DeleteFileCommand

 **/
public class DeleteFileCommand extends FileCommand{

    protected DeleteFileCommand(String baseUrl) throws MalformedURLException {
        super(baseUrl, ClientConstants.Endpoints.REMOVE_FILE);
        throw new NotImplementedException();
    }

    /*FIXME*/
    public void execute() {
        throw new NotImplementedException();
    }
}
