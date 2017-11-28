/**
 * Created by jorge at 11/11/17
 **/
package a16.yarfs.client.command.user;

import a16.yarfs.client.command.AbstractHttpCommand;

import java.net.MalformedURLException;

/**
 Class AbstractUserCommand

 **/
public abstract class AbstractUserCommand extends AbstractHttpCommand {
    protected AbstractUserCommand(String baseUrl, String endpoint) throws MalformedURLException {
        super(baseUrl, endpoint);
    }

}
