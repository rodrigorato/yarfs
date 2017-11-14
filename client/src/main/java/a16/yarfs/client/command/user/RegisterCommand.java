/**
 * Created by jorge at 11/11/17
 **/
package a16.yarfs.client.command.user;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.security.PublicKey;

/**
 Class RegisterCommand

 **/
public class RegisterCommand extends UserCommand{
    private String username;
    private String password;
    private PublicKey key;

    //FIXME
    public void execute() {
        throw new NotImplementedException();
    }
}
