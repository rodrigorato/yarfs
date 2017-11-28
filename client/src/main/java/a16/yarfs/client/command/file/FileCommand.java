/**
 * Created by jorge at 11/11/17
 **/
package a16.yarfs.client.command.file;

import a16.yarfs.client.command.AbstractHttpCommand;

import java.net.MalformedURLException;
import java.security.PrivateKey;

/**
 Class FileCommand

 **/
public abstract class FileCommand extends AbstractHttpCommand {
    private String filename;
    private String password;
    private PrivateKey key;

    protected FileCommand(String baseUrl, String endpoint) throws MalformedURLException {
        super(baseUrl, endpoint);
    }


    public abstract void execute();
}
