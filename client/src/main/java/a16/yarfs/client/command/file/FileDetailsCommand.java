/**
 * Created by jorge at 11/11/17
 **/
package a16.yarfs.client.command.file;

import a16.yarfs.client.ClientConstants;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.net.MalformedURLException;

/**
 Class FileDetailsCommand

 **/
public class FileDetailsCommand extends FileCommand{
    protected FileDetailsCommand(String baseUrl) throws MalformedURLException {
        super(baseUrl, ClientConstants.Endpoints.FILE_DETAILS);
        throw new NotImplementedException();
    }

    /*FIXME*/
    public void execute() {
        throw new NotImplementedException();
    }
}
