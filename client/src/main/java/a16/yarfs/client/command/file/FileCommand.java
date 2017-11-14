/**
 * Created by jorge at 11/11/17
 **/
package a16.yarfs.client.command.file;

import a16.yarfs.client.command.AbstractCommand;

import java.security.PrivateKey;

/**
 Class FileCommand

 **/
public abstract class FileCommand implements AbstractCommand {
    private String filename;
    private String password;
    private PrivateKey key;

    public abstract void execute();
}
